package com.keimons.platform.unit;

import java.util.Map;

public class Entry<K, V> implements Map.Entry<K, V> {

	private K key;

	private V value;

	public Entry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public void setKey(K key) {
		this.key = key;
	}

	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return this.value;
	}
}
