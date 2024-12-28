package mchorse.mclib.utils;

import net.minecraft.client.resources.I18n;

public enum Interpolation {
  LINEAR("linear") {
    public float interpolate(float a, float b, float x) {
      return Interpolations.lerp(a, b, x);
    }
    
    public double interpolate(double a, double b, double x) {
      return Interpolations.lerp(a, b, x);
    }
  },
  QUAD_IN("quad_in") {
    public float interpolate(float a, float b, float x) {
      return a + (b - a) * x * x;
    }
    
    public double interpolate(double a, double b, double x) {
      return a + (b - a) * x * x;
    }
  },
  QUAD_OUT("quad_out") {
    public float interpolate(float a, float b, float x) {
      return a - (b - a) * x * (x - 2.0F);
    }
    
    public double interpolate(double a, double b, double x) {
      return a - (b - a) * x * (x - 2.0D);
    }
  },
  QUAD_INOUT("quad_inout") {
    public float interpolate(float a, float b, float x) {
      x *= 2.0F;
      if (x < 1.0F)
        return a + (b - a) / 2.0F * x * x; 
      x--;
      return a - (b - a) / 2.0F * (x * (x - 2.0F) - 1.0F);
    }
    
    public double interpolate(double a, double b, double x) {
      x *= 2.0D;
      if (x < 1.0D)
        return a + (b - a) / 2.0D * x * x; 
      x--;
      return a - (b - a) / 2.0D * (x * (x - 2.0D) - 1.0D);
    }
  },
  CUBIC_IN("cubic_in") {
    public float interpolate(float a, float b, float x) {
      return a + (b - a) * x * x * x;
    }
    
    public double interpolate(double a, double b, double x) {
      return a + (b - a) * x * x * x;
    }
  },
  CUBIC_OUT("cubic_out") {
    public float interpolate(float a, float b, float x) {
      x--;
      return a + (b - a) * (x * x * x + 1.0F);
    }
    
    public double interpolate(double a, double b, double x) {
      x--;
      return a + (b - a) * (x * x * x + 1.0D);
    }
  },
  CUBIC_INOUT("cubic_inout") {
    public float interpolate(float a, float b, float x) {
      x *= 2.0F;
      if (x < 1.0F)
        return a + (b - a) / 2.0F * x * x * x; 
      x -= 2.0F;
      return a + (b - a) / 2.0F * (x * x * x + 2.0F);
    }
    
    public double interpolate(double a, double b, double x) {
      x *= 2.0D;
      if (x < 1.0D)
        return a + (b - a) / 2.0D * x * x * x; 
      x -= 2.0D;
      return a + (b - a) / 2.0D * (x * x * x + 2.0D);
    }
  },
  EXP_IN("exp_in") {
    public float interpolate(float a, float b, float x) {
      return a + (b - a) * (float)Math.pow(2.0D, (10.0F * (x - 1.0F)));
    }
    
    public double interpolate(double a, double b, double x) {
      return a + (b - a) * (float)Math.pow(2.0D, 10.0D * (x - 1.0D));
    }
  },
  EXP_OUT("exp_out") {
    public float interpolate(float a, float b, float x) {
      return a + (b - a) * (float)(-Math.pow(2.0D, (-10.0F * x)) + 1.0D);
    }
    
    public double interpolate(double a, double b, double x) {
      return a + (b - a) * (float)(-Math.pow(2.0D, -10.0D * x) + 1.0D);
    }
  },
  EXP_INOUT("exp_inout") {
    public float interpolate(float a, float b, float x) {
      if (x == 0.0F)
        return a; 
      if (x == 1.0F)
        return b; 
      x *= 2.0F;
      if (x < 1.0F)
        return a + (b - a) / 2.0F * (float)Math.pow(2.0D, (10.0F * (x - 1.0F))); 
      x--;
      return a + (b - a) / 2.0F * (float)(-Math.pow(2.0D, (-10.0F * x)) + 2.0D);
    }
    
    public double interpolate(double a, double b, double x) {
      if (x == 0.0D)
        return a; 
      if (x == 1.0D)
        return b; 
      x *= 2.0D;
      if (x < 1.0D)
        return a + (b - a) / 2.0D * (float)Math.pow(2.0D, 10.0D * (x - 1.0D)); 
      x--;
      return a + (b - a) / 2.0D * (float)(-Math.pow(2.0D, -10.0D * x) + 2.0D);
    }
  },
  BACK_IN("back_in") {
    public float interpolate(float a, float b, float x) {
      float c1 = 1.70158F;
      float c3 = 2.70158F;
      return Interpolations.lerp(a, b, 2.70158F * x * x * x - 1.70158F * x * x);
    }
    
    public double interpolate(double a, double b, double x) {
      double c1 = 1.70158D;
      double c3 = 2.70158D;
      return Interpolations.lerp(a, b, 2.70158D * x * x * x - 1.70158D * x * x);
    }
  },
  BACK_OUT("back_out") {
    public float interpolate(float a, float b, float x) {
      float c1 = 1.70158F;
      float c3 = 2.70158F;
      return Interpolations.lerp(a, b, 1.0F + 2.70158F * (float)Math.pow((x - 1.0F), 3.0D) + 1.70158F * (float)Math.pow((x - 1.0F), 2.0D));
    }
    
    public double interpolate(double a, double b, double x) {
      double c1 = 1.70158D;
      double c3 = 2.70158D;
      return Interpolations.lerp(a, b, 1.0D + 2.70158D * Math.pow(x - 1.0D, 3.0D) + 1.70158D * Math.pow(x - 1.0D, 2.0D));
    }
  },
  BACK_INOUT("back_inout") {
    public float interpolate(float a, float b, float x) {
      float c1 = 1.70158F;
      float c2 = 2.5949094F;
      float factor = (x < 0.5D) ? ((float)Math.pow((2.0F * x), 2.0D) * (7.189819F * x - 2.5949094F) / 2.0F) : (((float)Math.pow((2.0F * x - 2.0F), 2.0D) * (3.5949094F * (x * 2.0F - 2.0F) + 2.5949094F) + 2.0F) / 2.0F);
      return Interpolations.lerp(a, b, factor);
    }
    
    public double interpolate(double a, double b, double x) {
      double c1 = 1.70158D;
      double c2 = 2.5949095D;
      double factor = (x < 0.5D) ? (Math.pow(2.0D * x, 2.0D) * (7.189819D * x - 2.5949095D) / 2.0D) : ((Math.pow(2.0D * x - 2.0D, 2.0D) * (3.5949095D * (x * 2.0D - 2.0D) + 2.5949095D) + 2.0D) / 2.0D);
      return Interpolations.lerp(a, b, factor);
    }
  },
  ELASTIC_IN("elastic_in") {
    public float interpolate(float a, float b, float x) {
      float c4 = 2.0943952F;
      float factor = (x == 0.0F) ? 0.0F : ((x == 1.0F) ? 1.0F : (-((float)Math.pow(2.0D, (10.0F * x - 10.0F))) * (float)Math.sin(((x * 10.0F) - 10.75D) * 2.094395160675049D)));
      return Interpolations.lerp(a, b, factor);
    }
    
    public double interpolate(double a, double b, double x) {
      double c4 = 2.094395160675049D;
      double factor = (x == 0.0D) ? 0.0D : ((x == 1.0D) ? 1.0D : (-Math.pow(2.0D, 10.0D * x - 10.0D) * Math.sin((x * 10.0D - 10.75D) * 2.094395160675049D)));
      return Interpolations.lerp(a, b, factor);
    }
  },
  ELASTIC_OUT("elastic_out") {
    public float interpolate(float a, float b, float x) {
      float c4 = 2.0943952F;
      float factor = (x == 0.0F) ? 0.0F : ((x == 1.0F) ? 1.0F : ((float)Math.pow(2.0D, (-10.0F * x)) * (float)Math.sin(((x * 10.0F) - 0.75D) * 2.094395160675049D) + 1.0F));
      return Interpolations.lerp(a, b, factor);
    }
    
    public double interpolate(double a, double b, double x) {
      double c4 = 2.0943951023931953D;
      double factor = (x == 0.0D) ? 0.0D : ((x == 1.0D) ? 1.0D : (Math.pow(2.0D, -10.0D * x) * Math.sin((x * 10.0D - 0.75D) * 2.0943951023931953D) + 1.0D));
      return Interpolations.lerp(a, b, factor);
    }
  },
  ELASTIC_INOUT("elastic_inout") {
    public float interpolate(float a, float b, float x) {
      float c5 = 1.3962635F;
      float factor = (x == 0.0F) ? 0.0F : ((x == 1.0F) ? 1.0F : ((x < 0.5D) ? (-((float)Math.pow(2.0D, (20.0F * x - 10.0F)) * (float)Math.sin(((20.0F * x) - 11.125D) * 1.3962634801864624D)) / 2.0F) : ((float)Math.pow(2.0D, (-20.0F * x + 10.0F)) * (float)Math.sin(((20.0F * x) - 11.125D) * 1.3962634801864624D) / 2.0F + 1.0F)));
      return Interpolations.lerp(a, b, factor);
    }
    
    public double interpolate(double a, double b, double x) {
      double c5 = 1.3962634015954636D;
      double factor = (x == 0.0D) ? 0.0D : ((x == 1.0D) ? 1.0D : ((x < 0.5D) ? (-(Math.pow(2.0D, 20.0D * x - 10.0D) * Math.sin((20.0D * x - 11.125D) * 1.3962634015954636D)) / 2.0D) : (Math.pow(2.0D, -20.0D * x + 10.0D) * Math.sin((20.0D * x - 11.125D) * 1.3962634015954636D) / 2.0D + 1.0D)));
      return Interpolations.lerp(a, b, factor);
    }
  },
  BOUNCE_IN("bounce_in") {
    public float interpolate(float a, float b, float x) {
      return Interpolations.lerp(a, b, 1.0F - BOUNCE_OUT.interpolate(0.0F, 1.0F, 1.0F - x));
    }
    
    public double interpolate(double a, double b, double x) {
      return Interpolations.lerp(a, b, 1.0D - BOUNCE_OUT.interpolate(0.0D, 1.0D, 1.0D - x));
    }
  },
  BOUNCE_OUT("bounce_out") {
    public float interpolate(float a, float b, float x) {
      float factor, n1 = 7.5625F;
      float d1 = 2.75F;
      if (x < 0.36363637F) {
        factor = 7.5625F * x * x;
      } else if (x < 0.72727275F) {
        factor = 7.5625F * (x -= 0.54545456F) * x + 0.75F;
      } else if (x < 0.9090909090909091D) {
        factor = 7.5625F * (x -= 0.8181818F) * x + 0.9375F;
      } else {
        factor = 7.5625F * (x -= 0.95454544F) * x + 0.984375F;
      } 
      return Interpolations.lerp(a, b, factor);
    }
    
    public double interpolate(double a, double b, double x) {
      double factor, n1 = 7.5625D;
      double d1 = 2.75D;
      if (x < 0.36363636363636365D) {
        factor = 7.5625D * x * x;
      } else {
        factor = 7.5625D * (x -= 0.5454545454545454D) * x + 0.75D;
        factor = 7.5625D * (x -= 0.8181818181818182D) * x + 0.9375D;
        factor = 7.5625D * (x -= 0.9545454545454546D) * x + 0.984375D;
      } 
      return Interpolations.lerp(a, b, factor);
    }
  },
  BOUNCE_INOUT("bounce_inout") {
    public float interpolate(float a, float b, float x) {
      float factor = (x < 0.5D) ? ((1.0F - BOUNCE_OUT.interpolate(0.0F, 1.0F, 1.0F - 2.0F * x)) / 2.0F) : ((1.0F + BOUNCE_OUT.interpolate(0.0F, 1.0F, 2.0F * x - 1.0F)) / 2.0F);
      return Interpolations.lerp(a, b, factor);
    }
    
    public double interpolate(double a, double b, double x) {
      double factor = (x < 0.5D) ? ((1.0D - BOUNCE_OUT.interpolate(0.0D, 1.0D, 1.0D - 2.0D * x)) / 2.0D) : ((1.0D + BOUNCE_OUT.interpolate(0.0D, 1.0D, 2.0D * x - 1.0D)) / 2.0D);
      return Interpolations.lerp(a, b, factor);
    }
  };
  
  public final String key;
  
  Interpolation(String key) {
    this.key = key;
  }
  
  public String getName() {
    return I18n.format(getKey(), new Object[0]);
  }
  
  public String getKey() {
    return "mclib.interpolations." + this.key;
  }
  
  public abstract float interpolate(float paramFloat1, float paramFloat2, float paramFloat3);
  
  public abstract double interpolate(double paramDouble1, double paramDouble2, double paramDouble3);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\Interpolation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */