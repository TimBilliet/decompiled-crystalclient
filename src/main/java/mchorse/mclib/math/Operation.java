package mchorse.mclib.math;

import java.util.HashSet;
import java.util.Set;

public enum Operation {
  ADD("+", 1) {
    public double calculate(double a, double b) {
      return a + b;
    }
  },
  SUB("-", 1) {
    public double calculate(double a, double b) {
      return a - b;
    }
  },
  MUL("*", 2) {
    public double calculate(double a, double b) {
      return a * b;
    }
  },
  DIV("/", 2) {
    public double calculate(double a, double b) {
      return a / ((b == 0.0D) ? 1.0D : b);
    }
  },
  MOD("%", 2) {
    public double calculate(double a, double b) {
      return a % b;
    }
  },
  POW("^", 3) {
    public double calculate(double a, double b) {
      return Math.pow(a, b);
    }
  },
  AND("&&", 5) {
    public double calculate(double a, double b) {
      return (a != 0.0D && b != 0.0D) ? 1.0D : 0.0D;
    }
  },
  OR("||", 5) {
    public double calculate(double a, double b) {
      return (a != 0.0D || b != 0.0D) ? 1.0D : 0.0D;
    }
  },
  LESS("<", 5) {
    public double calculate(double a, double b) {
      return (a < b) ? 1.0D : 0.0D;
    }
  },
  LESS_THAN("<=", 5) {
    public double calculate(double a, double b) {
      return (a <= b) ? 1.0D : 0.0D;
    }
  },
  GREATER_THAN(">=", 5) {
    public double calculate(double a, double b) {
      return (a >= b) ? 1.0D : 0.0D;
    }
  },
  GREATER(">", 5) {
    public double calculate(double a, double b) {
      return (a > b) ? 1.0D : 0.0D;
    }
  },
  EQUALS("==", 5) {
    public double calculate(double a, double b) {
//      return null.equals(a, b) ? 1.0D : 0.0D;
      return Double.compare(a,b) == 0 ? 1.0D : 0.0D;
    }
  },
  NOT_EQUALS("!=", 5) {
    public double calculate(double a, double b) {
//      return !null.equals(a, b) ? 1.0D : 0.0D;
      return Double.compare(a,b) == 0 ? 0.0D : 1.0D;
    }
  };
  
  public static final Set<String> OPERATORS;
  
  public final String sign;
  
  public final int value;
  
  static {
    OPERATORS = new HashSet<>();
    for (Operation op : values())
      OPERATORS.add(op.sign); 
  }
  
  public static boolean equals(double a, double b) {
    return (Math.abs(a - b) < 1.0E-5D);
  }
  
  Operation(String sign, int value) {
    this.sign = sign;
    this.value = value;
  }
  
  public abstract double calculate(double paramDouble1, double paramDouble2);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\Operation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */