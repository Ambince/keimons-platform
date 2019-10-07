package com.keimons.platform.session;

import io.netty.util.internal.ConcurrentSet;

import java.util.Set;

/**
 * 会话管理器
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 */
public class SessionManager {

	// region 单例模式
	/**
	 * 管理器实例
	 */
	private static SessionManager instance;

	/**
	 * 单例模式，Session管理器
	 */
	private SessionManager() {

	}

	/**
	 * 会话管理实例
	 *
	 * @return 会话管理
	 */
	public static SessionManager getInstance() {
		if (instance == null) {
			synchronized (SessionManager.class) {
				if (instance == null) {
					instance = new SessionManager();
				}
			}
		}
		return instance;
	}
	// endregion

	/**
	 * 缓存整个游戏中所有的缓存
	 */
	private Set<Session> sessions = new ConcurrentSet<>();

	/**
	 * 增加一个客户端-服务器会话
	 *
	 * @param session 会话
	 */
	public void addSession(Session session) {
		sessions.add(session);
	}

	/**
	 * 移除客户端-服务器会话
	 *
	 * @param session 会话
	 */
	public void removeSession(Session session) {
		sessions.remove(session);
	}

	/**
	 * 关闭服务器
	 */
	public void shutdown() {
		for (Session session : sessions) {
			session.disconnect();
		}
	}
}