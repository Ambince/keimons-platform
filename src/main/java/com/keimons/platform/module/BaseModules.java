package com.keimons.platform.module;

import com.keimons.platform.annotation.APlayerData;
import com.keimons.platform.iface.IPlayerData;
import com.keimons.platform.iface.IRepeatedData;
import com.keimons.platform.iface.ISingularData;
import com.keimons.platform.log.LogService;
import com.keimons.platform.player.IModule;
import com.keimons.platform.unit.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家所有模块数据
 *
 * @author monkey1993
 * @version 1.0
 **/
public abstract class BaseModules<T> implements IPersistence<T> {

	/**
	 * 数据唯一标识
	 * <p>
	 * 当从数据库取出数据时，需要使用唯一标识符获取数据，当对数据进行持久化时，需要使用唯一标识符作为
	 * 主键，将数据存入数据库。
	 */
	protected T identifier;

	/**
	 * 玩家数据 Key:数据名称 Value:数据
	 */
	protected final ConcurrentHashMap<String, IModule<? extends IPlayerData>> modules = new ConcurrentHashMap<>();

	/**
	 * 最后活跃时间
	 */
	private volatile long activeTime = TimeUtil.currentTimeMillis();

	/**
	 * 默认构造函数
	 */
	public BaseModules() {
	}

	/**
	 * 构造函数
	 *
	 * @param identifier 数据唯一表示
	 */
	public BaseModules(T identifier) {
		this.identifier = identifier;
	}

	/**
	 * 模块数据
	 *
	 * @param moduleName 模块名称
	 * @param <V>        模块类型
	 * @return 模块数据
	 */
	@SuppressWarnings("unchecked")
	public <V extends IModule> V getModule(String moduleName) {
		return (V) modules.get(moduleName);
	}

	/**
	 * 获取玩家的一个模块
	 *
	 * @param clazz  模块名称
	 * @param dataId 数据唯一IDs
	 * @param <V>    模块类型
	 * @return 数据模块
	 */
	@SuppressWarnings("unchecked")
	public <V extends IRepeatedData> V getPlayerData(Class<V> clazz, Object dataId) {
		APlayerData annotation = clazz.getAnnotation(APlayerData.class);
		String moduleName = annotation.moduleName();
		IModule<?> module = modules.get(moduleName);
		return (V) module.getPlayerData(dataId);
	}

	/**
	 * 获取玩家的一个模块
	 *
	 * @param clazz 模块名称
	 * @param <V>   玩家数据类型
	 * @return 数据模块
	 */
	@SuppressWarnings("unchecked")
	public <V extends ISingularData> V getPlayerData(Class<V> clazz) {
		APlayerData annotation = clazz.getAnnotation(APlayerData.class);
		String moduleName = annotation.moduleName();
		IModule<?> module = modules.get(moduleName);
		return (V) module.getPlayerData(null);
	}

	/**
	 * 增加一个可重复的模块数据
	 *
	 * @param data 数据
	 */
	public abstract void addRepeatedData(IRepeatedData data);

	/**
	 * 增加一个非重复的模块数据
	 *
	 * @param data 数据
	 */
	public abstract void addSingularData(ISingularData data);

	/**
	 * 增加一个模块
	 *
	 * @param moduleName 模块名称
	 * @param module     模块
	 */
	protected void addModule(String moduleName, IModule<?> module) {
		modules.put(moduleName, module);
	}

	/**
	 * 获取玩家所有的模块数据
	 *
	 * @return 玩家所有模块数据
	 */
	public Map<String, IModule<? extends IPlayerData>> getModules() {
		return modules;
	}

	/**
	 * 检查玩家是否有该模块
	 *
	 * @param clazz 模块
	 * @return 是否有该模块
	 */
	public boolean hasModule(String clazz) {
		return modules.containsKey(clazz);
	}

	/**
	 * 检测玩家缺少的数据模块并添加该模块
	 */
	public void checkModule() {
		try {
			List<IPlayerData> init = new ArrayList<>();
			for (Map.Entry<String, Class<? extends IPlayerData>> entry : ModulesManager.classes.entrySet()) {
				// 判断模块是否需要被添加
				Class<? extends IPlayerData> clazz = entry.getValue();
				if (!hasModule(entry.getKey()) && !IRepeatedData.class.isAssignableFrom(clazz)) {
					IPlayerData data = clazz.newInstance();
					if (data instanceof IRepeatedData) {
						addRepeatedData((IRepeatedData) data);
					}
					if (data instanceof ISingularData) {
						addSingularData((ISingularData) data);
					}
					init.add(data);
				}
			}
			for (IPlayerData data : init) {
				data.init(this);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			LogService.error(e);
		}
	}

	public T getIdentifier() {
		return identifier;
	}

	public long getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(long activeTime) {
		this.activeTime = activeTime;
	}
}