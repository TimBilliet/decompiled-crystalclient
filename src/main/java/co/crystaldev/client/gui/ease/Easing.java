package co.crystaldev.client.gui.ease;

public interface Easing {
    IEasingFunction LINEAR = (percentComplete) -> percentComplete;
    IEasingFunction IN_QUAD = (percentComplete) -> (float) Math.pow(percentComplete, 2.0);
    IEasingFunction OUT_QUAD = (percentComplete) -> -percentComplete * (percentComplete - 2.0F);
    IEasingFunction IN_OUT_QUAD = (percentComplete) -> {
        double t = (percentComplete * 2.0F);
        if (t < 1.0D) {
            return (float) (0.5D * Math.pow(t, 2.0D));
        }
        t--;
        return (float) (-0.5D * (t * (t - 2.0D) - 1.0D));
    };

    IEasingFunction IN_CUBIC = (percentComplete) -> (float) Math.pow(percentComplete, 3.0);
    IEasingFunction OUT_CUBIC = (percentComplete) -> {
        double t = (percentComplete - 1.0F);
        return (float) (Math.pow(t, 3.0) + 1.0D);
    };
    IEasingFunction IN_OUT_CUBIC = (percentComplete) -> {
        double t = (percentComplete * 2.0F);
        if (t < 1.0D) {
            return (float) (0.5D * Math.pow(t, 3.0));
        }
        t -= 2.0D;
        return (float) (0.5D * (Math.pow(t, 3.0) + 2.0D));
    };

    IEasingFunction IN_QUART = (percentComplete) -> (float) Math.pow(percentComplete, 4.0);
    IEasingFunction OUT_QUART = (percentComplete) -> {
        double t = (percentComplete - 1.0F);
        return (float) -(Math.pow(t, 4.0) - 1.0D);
    };
    IEasingFunction IN_OUT_QUART = (percentComplete) -> {
        double t = (percentComplete * 2.0F);
        if (t < 1.0D) {
            return (float) (0.5D * Math.pow(t, 4.0));
        }
        t -= 2.0D;
        return (float) (-0.5D * (Math.pow(t, 4.0) - 2.0D));
    };

    IEasingFunction IN_QUINT = (percentComplete) -> (float) Math.pow(percentComplete, 5.0);
    IEasingFunction OUT_QUINT = (percentComplete) -> {
        double t = (percentComplete - 1.0F);
        return (float) (Math.pow(t, 5.0) + 1.0D);
    };
    IEasingFunction IN_OUT_QUINT = (percentComplete) -> {
        double t = (percentComplete * 2.0F);
        if (t < 1.0D) {
            return (float) (0.5D * Math.pow(t, 5.0));
        }
        t -= 2.0D;
        return (float) (0.5D * (Math.pow(t, 5.0) + 2.0D));
    };

    IEasingFunction IN_EXP = (percentComplete) -> (float) Math.pow(2.0, (10.0F * (percentComplete - 1.0F)));
    IEasingFunction OUT_EXP = (percentComplete) -> (float) (-Math.pow(2.0, (-10.0F * percentComplete)) + 1.0D);
    IEasingFunction IN_OUT_EXP = (percentComplete) -> {
        double t = (percentComplete * 2.0F);
        if (t < 1.0D) {
            return (float) (0.5D * Math.pow(2.0D, 10.0D * (t - 1.0D)));
        }
        t--;
        return (float) (0.5D * (-Math.pow(2.0D, -10.0D * t) + 2.0D));
    };

    IEasingFunction IN_CIRCULAR = (percentComplete) -> (float) -(Math.sqrt(1.0 - Math.pow(percentComplete, 2.0)) - 1.0D);
    IEasingFunction OUT_CIRCULAR = (percentComplete) -> {
        double t = (percentComplete - 1.0F);
        return (float) Math.sqrt(1.0 - Math.pow(t, 2.0));
    };
    IEasingFunction IN_OUT_CIRCULAR = (percentComplete) -> {
        double t = (percentComplete * 2.0F);
        if (t < 1.0D) {
            return (float) (-0.5D * (Math.sqrt(1.0 - Math.pow(t, 2.0)) - 1.0D));
        }
        t -= 2.0D;
        return (float) (0.5D * (Math.sqrt(1.0 - Math.pow(t, 2.0)) + 1.0D));
    };

    IEasingFunction IN_ELASTIC = (percentComplete) -> {
        double t = (percentComplete - 1.0F);
        return (float) (-Math.pow(2.0D, 10.0D * t) * Math.sin((t - 0.07500000298023224D) * 6.2831854820251465D / 0.30000001192092896D));
    };
    IEasingFunction OUT_ELASTIC = (percentComplete) -> (float) (Math.pow(2.0D, (-10.0F * percentComplete)) * Math.sin(((percentComplete - 0.075F) * 6.2831855F / 0.3F)) + 1.0D);
    IEasingFunction IN_OUT_ELASTIC = (percentComplete) -> {
        double t = (percentComplete * 2.0F - 1.0F);
        return (t < 0.0D)
                ? (float) (0.5D * -Math.pow(2.0D, 10.0D * t) * Math.sin((t - 0.11249999701976776D) * 6.2831854820251465D / 0.44999998807907104D))
                : (float) (0.5D * Math.pow(2.0D, -10.0D * t) * Math.sin((t - 0.11249999701976776D) * 6.2831854820251465D / 0.44999998807907104D) + 1.0D);
    };

    IEasingFunction IN_BOUNCE = percentComplete -> 1.0F - Easing.OUT_BOUNCE.getValue(1.0F - percentComplete);
    IEasingFunction OUT_BOUNCE = (percentComplete) -> {
        float t = percentComplete;
        if (t < 0.36363637F) {
            return 7.5625F * t * t;
        } else if (t < 0.72727275F) {
            t -= 0.54545456F;
            return 7.5625F * t * t + 0.75F;
        } else if (t < 0.9090909090909091D) {
            t -= 0.8181818F;
            return 7.5625F * t * t + 0.9375F;
        } else {
            t -= 0.95454544F;
            return 7.5625F * t * t + 0.984375F;
        }
    };

    IEasingFunction IN_OUT_BOUNCE = (percentComplete) -> (percentComplete < 0.5F)
            ? (IN_BOUNCE.getValue(percentComplete * 2.0F) * 0.5F)
            : (OUT_BOUNCE.getValue(percentComplete * 2.0F - 1.0F) * 0.5F + 0.5F);
}
