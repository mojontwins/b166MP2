package net.minecraft.src;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ModAction implements Runnable {
	protected List handlerObjects = new ArrayList();
	protected List handlerMethods = new ArrayList();
	protected Class[] params;
	public String name;
	public Object data;

	public ModAction(String name_, Class... params_) {
		this.name = name_;
		this.params = params_;
	}

	public ModAction(Object o, String method, Class... params_) {
		this.name = method;
		this.params = params_;
		this.addHandler(o, method);
	}

	public ModAction(Object o, String method, String _name, Class... params_) {
		this.name = _name;
		this.params = params_;
		this.addHandler(o, method);
	}

	public ModAction(Object o, String method, Object _data, Class... params_) {
		this.name = method;
		this.data = _data;
		this.params = params_;
		this.addHandler(o, method);
	}

	public ModAction addHandler(Object o, String method) {
		try {
			this.GetMethodRecursively(o, method);
		} catch (Exception exception6) {
			exception6.printStackTrace();
			throw new RuntimeException("invalid method parameters");
		}

		for(int i = 0; i < this.handlerObjects.size(); ++i) {
			Object oo = this.handlerObjects.get(i);
			String omethod = (String)this.handlerMethods.get(i);
			if(oo == o && omethod.equals(method)) {
				System.err.println(String.format("WARNING: event handler is already registered: %s: %s.%s", new Object[]{this.name, o.getClass().getName(), method}));
				return this;
			}
		}

		this.handlerObjects.add(o);
		this.handlerMethods.add(method);
		return this;
	}

	public Object[] call(Object... args) {
		Object[] returns = new Object[this.handlerObjects.size()];

		for(int i = 0; i < this.handlerObjects.size(); ++i) {
			Object o = this.handlerObjects.get(i);
			String method = (String)this.handlerMethods.get(i);

			try {
				Method e = this.GetMethodRecursively(o, method);
				returns[i] = e.invoke(o instanceof Class ? null : o, args);
			} catch (Exception exception7) {
				exception7.printStackTrace();
				throw new RuntimeException("error calling callback");
			}
		}

		return returns;
	}

	public boolean hasHandler(Object o, String method) {
		for(int i = 0; i < this.handlerObjects.size(); ++i) {
			Object oo = this.handlerObjects.get(i);
			String omethod = (String)this.handlerMethods.get(i);
			if(oo == o && omethod.equals(method)) {
				return true;
			}
		}

		return false;
	}

	public boolean removeHandler(Object o, String method) {
		for(int i = 0; i < this.handlerObjects.size(); ++i) {
			Object oo = this.handlerObjects.get(i);
			String omethod = (String)this.handlerMethods.get(i);
			if(oo == o && omethod.equals(method)) {
				this.handlerObjects.remove(i);
				this.handlerMethods.remove(i);
				return true;
			}
		}

		return false;
	}

	Method GetMethodRecursively(Object o, String method) {
		Class currentclass = o instanceof Class ? (Class)o : o.getClass();

		while(true) {
			try {
				if(currentclass == null) {
					return null;
				}

				Method returnval = currentclass.getDeclaredMethod(method, this.params);
				if(returnval != null) {
					returnval.setAccessible(true);
					return returnval;
				}
			} catch (Throwable throwable5) {
			}

			currentclass = currentclass.getSuperclass();
		}
	}

	public void run() {
		this.call(new Object[0]);
	}
}
