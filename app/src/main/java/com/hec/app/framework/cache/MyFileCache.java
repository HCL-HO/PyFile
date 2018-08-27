package com.hec.app.framework.cache;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

public class MyFileCache implements MyCache {

	private static final String LOG_TAG = "HecFileCache";

	private static final Object lruLock = new Object();
	private static final MyFileCache instance = new MyFileCache();

	private static String cacheRoot;
	private LinkedList<String> lru;

	private MyFileCache() {
		this.lru = new LinkedList<String>();

		// TODO clean expired cache.
	}

	public static void install(Context context) {
		Log.i("store" , "external "+Environment.isExternalStorageRemovable());
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// SD-card available
			//cacheRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/"
			//		+ context.getPackageName() + "/cache";
			cacheRoot = context.getApplicationInfo().dataDir;
			Log.i("store","cacheRoot "+cacheRoot);
			File file = new File(cacheRoot);
			boolean a = file.mkdirs();
			boolean b = file.isDirectory();
		} else {
			cacheRoot = context.getCacheDir().getAbsolutePath();
		}
	}

	public static MyFileCache getInstance() {
		return instance;
	}

	@Override
	public <T> T get(String key) {
		File file = new File(cacheRoot, escapeFileName(key));
		if (file.exists()) {
			FileInputStream stream = null;
			ObjectInputStream objectStream = null;
			try {
				stream = new FileInputStream(file);
				objectStream = new ObjectInputStream(stream);
				@SuppressWarnings("unchecked")
				T value = (T) objectStream.readObject();

				// Update position within LRU.
				String filePath = file.getAbsolutePath();
				synchronized (lruLock) {
					lru.remove(filePath);
					lru.addLast(filePath);
				}

				return value;
			} catch (FileNotFoundException ex) {
				// Will not happen.
			} catch (StreamCorruptedException ex) {
				remove(key);
			} catch (IOException ex) {
				remove(key);
			} catch (ClassNotFoundException ex) {
				// Will not happen.
			} finally {
				if (objectStream != null) {
					try {
						objectStream.close();
					} catch (IOException ex) {
						// Do nothing.
					}
				}
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException ex) {
						// Do nothing.
					}
				}
			}
		}
		return null;
	}

	@Override
	public <T> T get(String key, T defaultValue) {
		T item = get(key);
		if (item == null) {
			return defaultValue;
		} else {
			return item;
		}
	}

	@Override
	public <T> void put(String key, T value) {
		File file = new File(cacheRoot, escapeFileName(key));
		boolean fileExists = true;
		try {
			fileExists = !file.createNewFile();
		} catch (IOException ex) {
			// Will not happen.
		}

		FileOutputStream stream = null;
		ObjectOutputStream objectStream = null;
		try {
			stream = new FileOutputStream(file);
			objectStream = new ObjectOutputStream(stream);
			objectStream.writeObject(value);

			if (!fileExists) {
				synchronized (lruLock) {
					lru.addLast(file.getAbsolutePath());
				}
			}
		} catch (FileNotFoundException ex) {
			// Will not happen.
		} catch (IOException ex) {
			//g.i(LOG_TAG, ex.getMessage());
			// In case this happens, just ignore it.
		} finally {
			if (objectStream != null) {
				try {
					objectStream.close();
				} catch (IOException ex) {
					// Do nothing.
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ex) {
					// Do nothing.
				}
			}
		}
	}

	@Override
	public void remove(String key) {
		File file = new File(cacheRoot, escapeFileName(key));
		if (!file.delete()) {
			file.deleteOnExit();
		}
		synchronized (lruLock) {
			lru.remove(file.getAbsolutePath());
		}
	}

	@Override
	public boolean exists(String key) {
		return new File(cacheRoot, escapeFileName(key)).exists();
	}

	public InputStream getReadStream(String key) {
		File file = new File(cacheRoot, escapeFileName(key));
		if (file.exists()) {
			try {
				// Update position within LRU.
				String filePath = file.getAbsolutePath();
				synchronized (lruLock) {
					lru.remove(filePath);
					lru.addLast(filePath);
				}

				return new FileInputStream(file);
			} catch (FileNotFoundException ex) {
				// Return null in this case.
			}
		}
		return null;
	}

	public OutputStream getWriteStream(String key, int length) {
		File file = new File(cacheRoot, escapeFileName(key));
		try {
			boolean fileExists = !file.createNewFile();

			if (!fileExists) {
				synchronized (lruLock) {
					lru.addLast(file.getAbsolutePath());
				}
			}

			return new CacheOutputStream(file, length);
		} catch (IOException ex) {
			// Will not happen.
		}
		return null;
	}

	private String escapeFileName(String key) {
		return key.replace('/', '_');
	}

	private static class CacheOutputStream extends FileOutputStream {
		private int length;
		private FileChannel channel;

		public CacheOutputStream(File file, int length) throws FileNotFoundException {
			super(file);
			this.length = length;
			this.channel = getChannel();
		}

		@Override
		public void write(byte[] buffer) throws IOException {
			super.write(buffer);
			if (channel.size() >= length) {
				close();
			}
		}

		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException {
			super.write(buffer, offset, count);
			if (channel.size() >= length) {
				close();
			}
		}

		@Override
		public void write(int oneByte) throws IOException {
			super.write(oneByte);
			if (channel.size() >= length) {
				close();
			}
		}
	}
}