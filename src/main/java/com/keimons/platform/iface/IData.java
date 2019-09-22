package com.keimons.platform.iface;

/**
 * 玩家数据
 *
 * @author monkey1993
 * @version 1.0
 * @date 2019-09-22
 * @since 1.8
 */
public interface IData {

	/**
	 * 解码 数据最原始的解码操作
	 * <p>
	 * 数据从数据库中读取出来之后进行初始化工作
	 * 所有的数据初始化工作都应该在这里完成
	 *
	 * @return 数据解码是否成功
	 */
	default boolean decode() {
		return true;
	}

	/**
	 * 编码 数据最后的编码操作
	 * <p>
	 * 数据从内存存入数据库中的最后一步操作
	 * 因为程序中有一部分使用的是抽象类，无法通过反射创建抽象类对象，需要将抽象类转成（类型+数据）存入数据库
	 * 解码时，根据类型解码成对应的对象
	 *
	 * @return 数据编码是否成功
	 */
	default boolean encode() {
		return true;
	}
}