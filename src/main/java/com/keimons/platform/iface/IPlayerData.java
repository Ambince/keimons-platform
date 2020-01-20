package com.keimons.platform.iface;

import com.keimons.platform.module.BaseModules;

/**
 * 玩家数据模块
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 */
public interface IPlayerData extends ISerializable, ILoaded, IDataVersion {

	/**
	 * 初始化(当且仅当对象被创建时调用)
	 * <p>
	 * 由于对象的创建 {@link com.keimons.platform.unit.CodeUtil} 是由反射创建的
	 * 所以，要对对象的一些内部变量进行初始化工作
	 * 对数据进行初始化，仅在对象创建时进行初始化工作
	 * 当数据已经被初始化后，禁止进行第二次初始化
	 *
	 * @param player 玩家
	 */
	void init(BaseModules player);
}