package de.matthiasmann.twl.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractMathInterpreter implements SimpleMathParser.Interpreter {
	private final ArrayList stack = new ArrayList();
	private final HashMap functions = new HashMap();

	public AbstractMathInterpreter() {
		this.registerFunction("min", new AbstractMathInterpreter.FunctionMin());
		this.registerFunction("max", new AbstractMathInterpreter.FunctionMax());
	}

	public final void registerFunction(String name, AbstractMathInterpreter.Function function) {
		if(function == null) {
			throw new NullPointerException("function");
		} else {
			this.functions.put(name, function);
		}
	}

	public Number execute(String str) throws ParseException {
		this.stack.clear();
		SimpleMathParser.interpret(str, this);
		if(this.stack.size() != 1) {
			throw new IllegalStateException("Expected one return value on the stack");
		} else {
			return this.popNumber();
		}
	}

	public int[] executeIntArray(String str) throws ParseException {
		this.stack.clear();
		int count = SimpleMathParser.interpretArray(str, this);
		if(this.stack.size() != count) {
			throw new IllegalStateException("Expected " + count + " return values on the stack");
		} else {
			int[] result = new int[count];

			for(int i = count; i-- > 0; result[i] = this.popNumber().intValue()) {
			}

			return result;
		}
	}

	public Object executeCreateObject(String str, Class type) throws ParseException {
		this.stack.clear();
		int count = SimpleMathParser.interpretArray(str, this);
		if(this.stack.size() != count) {
			throw new IllegalStateException("Expected " + count + " return values on the stack");
		} else if(count == 1 && type.isInstance(this.stack.get(0))) {
			return type.cast(this.stack.get(0));
		} else {
			Constructor[] constructor7;
			int i6 = (constructor7 = type.getConstructors()).length;

			for(int i5 = 0; i5 < i6; ++i5) {
				Constructor c = constructor7[i5];
				Class[] params = c.getParameterTypes();
				if(params.length == count) {
					boolean match = true;

					for(int ex = 0; ex < count; ++ex) {
						if(!ClassUtils.isParamCompatible(params[ex], this.stack.get(ex))) {
							match = false;
							break;
						}
					}

					if(match) {
						try {
							return type.cast(c.newInstance(this.stack.toArray(new Object[count])));
						} catch (Exception exception11) {
							Logger.getLogger(AbstractMathInterpreter.class.getName()).log(Level.SEVERE, "can\'t instanciate object", exception11);
						}
					}
				}
			}

			throw new IllegalArgumentException("Can\'t construct a " + type + " from expression: \"" + str + "\"");
		}
	}

	protected void push(Object obj) {
		this.stack.add(obj);
	}

	protected Object pop() {
		int size = this.stack.size();
		if(size == 0) {
			throw new IllegalStateException("stack underflow");
		} else {
			return this.stack.remove(size - 1);
		}
	}

	protected Number popNumber() {
		Object obj = this.pop();
		if(obj instanceof Number) {
			return (Number)obj;
		} else {
			throw new IllegalStateException("expected number on stack - found: " + (obj != null ? obj.getClass() : "null"));
		}
	}

	public void loadConst(Number n) {
		this.push(n);
	}

	public void add() {
		Number b = this.popNumber();
		Number a = this.popNumber();
		boolean isFloat = isFloat(a) || isFloat(b);
		if(isFloat) {
			this.push(a.floatValue() + b.floatValue());
		} else {
			this.push(a.intValue() + b.intValue());
		}

	}

	public void sub() {
		Number b = this.popNumber();
		Number a = this.popNumber();
		boolean isFloat = isFloat(a) || isFloat(b);
		if(isFloat) {
			this.push(a.floatValue() - b.floatValue());
		} else {
			this.push(a.intValue() - b.intValue());
		}

	}

	public void mul() {
		Number b = this.popNumber();
		Number a = this.popNumber();
		boolean isFloat = isFloat(a) || isFloat(b);
		if(isFloat) {
			this.push(a.floatValue() * b.floatValue());
		} else {
			this.push(a.intValue() * b.intValue());
		}

	}

	public void div() {
		Number b = this.popNumber();
		Number a = this.popNumber();
		boolean isFloat = isFloat(a) || isFloat(b);
		if(isFloat) {
			if(Math.abs(b.floatValue()) == 0.0F) {
				throw new IllegalStateException("division by zero");
			}

			this.push(a.floatValue() / b.floatValue());
		} else {
			if(b.intValue() == 0) {
				throw new IllegalStateException("division by zero");
			}

			this.push(a.intValue() / b.intValue());
		}

	}

	public void accessArray() {
		Number idx = this.popNumber();
		Object obj = this.pop();
		if(obj == null) {
			throw new IllegalStateException("null pointer");
		} else if(!obj.getClass().isArray()) {
			throw new IllegalStateException("array expected");
		} else {
			try {
				this.push(Array.get(obj, idx.intValue()));
			} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException4) {
				throw new IllegalStateException("array index out of bounds", arrayIndexOutOfBoundsException4);
			}
		}
	}

	public void accessField(String field) {
		Object obj = this.pop();
		if(obj == null) {
			throw new IllegalStateException("null pointer");
		} else {
			Object result = this.accessField(obj, field);
			this.push(result);
		}
	}

	protected Object accessField(Object obj, String field) {
		try {
			if(obj.getClass().isArray()) {
				if("length".equals(field)) {
					return Array.getLength(obj);
				}
			} else {
				Method[] method6;
				int i5 = (method6 = obj.getClass().getMethods()).length;

				for(int i4 = 0; i4 < i5; ++i4) {
					Method ex = method6[i4];
					if((ex.getModifiers() & 8) == 0 && ex.getReturnType() != Void.TYPE && ex.getParameterTypes().length == 0 && (cmpName(ex, field, "get") || cmpName(ex, field, "is"))) {
						return ex.invoke(obj, new Object[0]);
					}
				}
			}

			throw new IllegalStateException("unknown field \'" + field + "\' of class \'" + obj.getClass() + "\'");
		} catch (Throwable throwable7) {
			throw new IllegalStateException("error accessing field \'" + field + "\' of class \'" + obj.getClass() + "\'", throwable7);
		}
	}

	private static boolean cmpName(Method m, String fieldName, String prefix) {
		String methodName = m.getName();
		int prefixLength = prefix.length();
		int fieldNameLength = fieldName.length();
		return methodName.length() == prefixLength + fieldNameLength && methodName.startsWith(prefix) && methodName.charAt(prefixLength) == Character.toUpperCase(fieldName.charAt(0)) && methodName.regionMatches(prefixLength + 1, fieldName, 1, fieldNameLength - 1);
	}

	public void callFunction(String name, int args) {
		Object[] values = new Object[args];

		for(int function = args; function-- > 0; values[function] = this.pop()) {
		}

		AbstractMathInterpreter.Function abstractMathInterpreter$Function5 = (AbstractMathInterpreter.Function)this.functions.get(name);
		if(abstractMathInterpreter$Function5 == null) {
			throw new IllegalArgumentException("Unknown function");
		} else {
			this.push(abstractMathInterpreter$Function5.execute(values));
		}
	}

	protected static boolean isFloat(Number n) {
		return !(n instanceof Integer);
	}

	public interface Function {
		Object execute(Object... object1);
	}

	static class FunctionMax extends AbstractMathInterpreter.NumberFunction {
		protected Object execute(int... values) {
			int result = values[0];

			for(int i = 1; i < values.length; ++i) {
				result = Math.max(result, values[i]);
			}

			return result;
		}

		protected Object execute(float... values) {
			float result = values[0];

			for(int i = 1; i < values.length; ++i) {
				result = Math.max(result, values[i]);
			}

			return result;
		}
	}

	static class FunctionMin extends AbstractMathInterpreter.NumberFunction {
		protected Object execute(int... values) {
			int result = values[0];

			for(int i = 1; i < values.length; ++i) {
				result = Math.min(result, values[i]);
			}

			return result;
		}

		protected Object execute(float... values) {
			float result = values[0];

			for(int i = 1; i < values.length; ++i) {
				result = Math.min(result, values[i]);
			}

			return result;
		}
	}

	public abstract static class NumberFunction implements AbstractMathInterpreter.Function {
		protected abstract Object execute(int... i1);

		protected abstract Object execute(float... f1);

		public Object execute(Object... args) {
			Object[] object5 = args;
			int i4 = args.length;

			int i;
			for(i = 0; i < i4; ++i) {
				Object values = object5[i];
				if(!(values instanceof Integer)) {
					float[] values1 = new float[args.length];

					for(int i1 = 0; i1 < values1.length; ++i1) {
						values1[i1] = ((Number)args[i1]).floatValue();
					}

					return this.execute(values1);
				}
			}

			int[] i8 = new int[args.length];

			for(i = 0; i < i8.length; ++i) {
				i8[i] = ((Number)args[i]).intValue();
			}

			return this.execute(i8);
		}
	}
}
