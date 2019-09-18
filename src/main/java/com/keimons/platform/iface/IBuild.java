package com.keimons.platform.iface;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.keimons.platform.player.AbsPlayer;

/**
 * 如果这个道具需要展示给客户端看的话，需要实现这个接口
 */
public interface IBuild {

	/**
	 * 构造物品的客户端数据
	 *
	 * @return 物品数据
	 */
	default MessageLite build() {
		return null;
	}

	/**
	 * 构造物品的客户端数据
	 * <p>
	 * 公共玩家数据，这一部分数据由两部分构成
	 * 1.玩家私有数据
	 * 2.玩家和其他玩家共有数据
	 * <p>
	 * eg:
	 * 私有数据：好友友情值已经领取的礼包
	 * 共有数据：好友友情值
	 *
	 * @param bytes 公共部分数据
	 * @return 物品数据
	 */
	default MessageLite build(ByteString bytes) throws InvalidProtocolBufferException {
		return build();
	}

	/**
	 * 构造物品的客户端数据
	 * <p>
	 * 公共玩家数据，这一部分数据由两部分构成
	 * 1.玩家私有数据
	 * 2.玩家和其他玩家共有数据
	 * <p>
	 * eg:
	 * 私有数据：好友友情值已经领取的礼包
	 * 共有数据：好友友情值
	 *
	 * @param player 玩家
	 * @param bytes  公共部分数据
	 * @return 物品数据
	 */
	default MessageLite build(AbsPlayer player, ByteString bytes) throws InvalidProtocolBufferException {
		return build(bytes);
	}
}