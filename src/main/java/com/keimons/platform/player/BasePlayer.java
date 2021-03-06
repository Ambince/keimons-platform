package com.keimons.platform.player;

import com.keimons.platform.annotation.APlayerData;
import com.keimons.platform.log.LogService;
import com.keimons.platform.module.IModule;
import com.keimons.platform.module.IRepeatedModule;
import com.keimons.platform.module.ISingularModule;
import com.keimons.platform.session.Session;
import com.keimons.platform.unit.TimeUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

/**
 * 玩家基类
 *
 * @param <T> 玩家标识类型
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public abstract class BasePlayer<T> implements IPlayer<T> {

	/**
	 * 数据唯一标识
	 * <p>
	 * 当从数据库取出数据时，需要使用唯一标识符获取数据，当对数据进行持久化时，需要使用唯一标识符作为
	 * 主键，将数据存入数据库。
	 */
	protected T identifier;

	/**
	 * 数据是否已经加载
	 * <p>
	 * 为了防止数据被重复加载，所以需要一个标识符，标识数据是否已经被加载了。如果数据已经被
	 * 加载，则不会向这个{@code DefaultPlayer}中进行二次加载，以防止覆盖之前的对象。
	 */
	private boolean loaded;

	/**
	 * 玩家数据 Key:数据名称 Value:数据
	 */
	protected final ConcurrentHashMap<String, IModule<? extends IPlayerData>> modules = new ConcurrentHashMap<>();

	/**
	 * 已经初始化过的模块名称
	 * <p>
	 * 系统允许只加载用户的一部分数据。{@link #load(Class[])}加载数据时，如果数据没有完全加载，{@link #get(Class)}获取数据时，应该获取到的是
	 * {@code null}，如果该模块未曾初始化，那么则可以对该模块进行初始化。
	 * <p>
	 * 警告：如果模块已经初始化，再次初始化模块，会导致数据被覆盖。
	 */
	protected final CopyOnWriteArraySet<String> moduleNames = new CopyOnWriteArraySet<>();

	/**
	 * 最后活跃时间
	 */
	protected volatile long activeTime = TimeUtil.currentTimeMillis();

	/**
	 * 客户端-服务器会话
	 * <p>
	 * 客户端和服务器相互绑定，向服务器发送数据通过客户端完成
	 */
	protected Session session;

	/**
	 * 默认构造函数
	 */
	public BasePlayer() {
	}

	/**
	 * 构造函数
	 *
	 * @param identifier 数据唯一表示
	 */
	public BasePlayer(T identifier) {
		this.identifier = identifier;
	}

	@Override
	public void add(IPlayerData data) {
		if (data instanceof ISingularPlayerData) {
			addSingularData((ISingularPlayerData) data);
		}
		if (data instanceof IRepeatedPlayerData) {
			addRepeatedData((IRepeatedPlayerData<?>) data);
		}
	}

	/**
	 * 获取玩家的一个模块
	 * <p>
	 * 如果玩家已经被完全加载，如果缺少这个模块，则补充这个模块，如果玩家没有被完全加载，则不能
	 *
	 * @param clazz 模块名称
	 * @param <V>   玩家数据类型
	 * @return 数据模块
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <V extends ISingularPlayerData> V get(Class<V> clazz) {
		APlayerData annotation = clazz.getAnnotation(APlayerData.class);
		String moduleName = annotation.moduleName();
		ISingularModule<?> module = (ISingularModule<?>) modules.get(moduleName);
		if (module == null && !modules.containsKey(moduleName)) {
			synchronized (this) {
				if (!modules.containsKey(moduleName)) {
					try {
						ISingularPlayerData data = clazz.getDeclaredConstructor().newInstance();
						data.init(this);
						addSingularData(data);
						moduleNames.add(moduleName);
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						LogService.error(e);
					}
				}
			}
			module = (ISingularModule<?>) modules.get(moduleName);
		}
		if (module == null) {
			return null;
		}
		return (V) module.get();
	}

	/**
	 * 获取玩家的一个模块
	 *
	 * @param clazz  模块名称
	 * @param dataId 数据唯一IDs
	 * @param <V>    模块类型
	 * @return 数据模块
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <V extends IRepeatedPlayerData<?>> V get(Class<V> clazz, Object dataId) {
		APlayerData annotation = clazz.getAnnotation(APlayerData.class);
		String moduleName = annotation.moduleName();
		IRepeatedModule<?> module = (IRepeatedModule<?>) modules.get(moduleName);
		if (module == null) {
			return null;
		}
		return (V) module.get(dataId);
	}

	/**
	 * 移除玩家的一个模块
	 *
	 * @param clazz  模块名称
	 * @param dataId 数据唯一IDs
	 * @param <V>    模块类型
	 * @return 数据模块
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <V extends IRepeatedPlayerData<?>> V remove(Class<V> clazz, Object dataId) {
		APlayerData annotation = clazz.getAnnotation(APlayerData.class);
		String moduleName = annotation.moduleName();
		IRepeatedModule<?> module = (IRepeatedModule<?>) modules.get(moduleName);
		return (V) module.remove(dataId);
	}

	/**
	 * 获取数据模块
	 *
	 * @param moduleName 模块名字
	 * @param function   新建模块
	 * @param <V>        模块类型
	 * @return 模块
	 */
	@SuppressWarnings("unchecked")
	protected <V extends IModule<? extends IPlayerData>> V computeIfAbsent(
			String moduleName, Function<String, V> function) {
		Objects.requireNonNull(function);
		return (V) modules.computeIfAbsent(moduleName, function);
	}

	/**
	 * 增加一个可重复的模块数据
	 *
	 * @param data 数据
	 */
	public abstract void addRepeatedData(IRepeatedPlayerData<?> data);

	/**
	 * 增加一个非重复的模块数据
	 *
	 * @param data 数据
	 */
	public abstract void addSingularData(ISingularPlayerData data);

	@Override
	public boolean hasModules(Class<? extends IPlayerData>[] classes) {
		for (Class<? extends IPlayerData> clazz : classes) {
			APlayerData annotation = clazz.getAnnotation(APlayerData.class);
			if (annotation == null) {
				return false;
			}
			if (!modules.containsKey(annotation.moduleName())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void clearIfNot(Class<? extends IPlayerData>[] classes) {
		Set<String> moduleNames = new HashSet<>(classes.length);
		for (Class<? extends IPlayerData> clazz : classes) {
			APlayerData annotation = clazz.getAnnotation(APlayerData.class);
			if (annotation == null) {
				continue;
			}
			moduleNames.add(annotation.moduleName());
		}
		for (String moduleName : modules.keySet()) {
			if (!moduleNames.contains(moduleName)) {
				modules.remove(moduleName);
			}
		}
	}

	@Override
	public T getIdentifier() {
		return this.identifier;
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void setActiveTime(long activeTime) {
		this.activeTime = activeTime;
	}

	@Override
	public long getActiveTime() {
		return activeTime;
	}
}