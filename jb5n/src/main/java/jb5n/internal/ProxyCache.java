package jb5n.internal;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProxyCache {
	private Map<ProxyKey, Object> cache = new HashMap<ProxyKey, Object>();

	public static class ProxyKey {
		private final Class<?> clazz;
		private final Locale locale;
		private final ClassLoader classLoader;

		public ProxyKey(Class<?> clazz, Locale locale, ClassLoader classLoader) {
			this.clazz = clazz;
			this.locale = locale;
			this.classLoader = classLoader;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((classLoader == null) ? 0 : classLoader.hashCode());
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((locale == null) ? 0 : locale.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ProxyKey other = (ProxyKey) obj;
			if (classLoader != other.classLoader) {
				return false;
			}
			if (clazz != other.clazz) {
				return false;
			}
			if (locale == null) {
				if (other.locale != null) {
					return false;
				}
			} else if (!locale.equals(other.locale)) {
				return false;
			}
			return true;
		}
	}

	public synchronized Object get(ProxyKey key) {
		return this.cache.get(key);
	}

	public synchronized Object put(ProxyKey key, Object value) {
		return this.cache.put(key, value);
	}
}
