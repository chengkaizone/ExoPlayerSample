package com.tony.exoplayersample.model;

/**
 * Created by tony on 2017/11/30.
 */
public class ScientificModel {

    // X²
    public double x_2;
    // X³
    public double x_3;
    // 2^x
    public double _2_x;
    // 10^x
    public double _10_x;
    // e^x
    public double e_x;
    // √x
    public double x__2;
    // ³√x
    public double x__3;
    // x^y  第一个参数为结果 第二个参数为y
    public double[] x_y;
    // y^x 第一个参数为结果 第二个参数为y
    public double[] y_x;
    //var x__y: Double!
    // sin(x) 三角函数
    public double sinx;
    // cos(x)
    public double cosx;
    // tan(x)
    public double tanx;
    // asin(x) 根据值获取弧度 * 180
    public double asinx;
    // acos(x) 根据值获取弧度 * 180
    public double acosx;
    // atan(x) 根据值获取弧度 * 180
    public double atanx;
    // e为底的对数
    public double lnx;
    // 2为底
    public double log2x;
    // 10为底
    public double log10x;
    // logx(y) 第一个参数为结果 第二个参数为y
    public double[] logxy;
    // π
    public double pi;
    // 1/x
    public double _1_d_x;
    // x!
    public double x___;

    public static ScientificModel calculate(double x) {

        ScientificModel model = new ScientificModel();

        model.x_2 = Math.pow(x, 2);
        model.x_3 = Math.pow(x, 3);

        model.x_y = new double[2];
        model.y_x = new double[2];
        model.logxy = new double[2];

        model.x_y[1] = Double.NaN;
        model.y_x[1] = Double.NaN;
        model.logxy[1] = Double.NaN;

        model._2_x = Math.pow(2, x);
        model._10_x = Math.pow(10, x);
        model.e_x = Math.exp(x);
        model.x__2 = Math.sqrt(x);
        model.x__3 = Math.cbrt(x);

        // 三角函数 先计算出弧度
        double radian = x / 180.0 * Math.PI;

        model.pi = radian;
        model.sinx = Math.sin(radian);
        model.cosx = Math.cos(radian);
        model.atanx = Math.atan(x) * 180.0 / Math.PI;

        if (Math.abs(x % 180) != 90) {
            // sinx / cosx
            model.tanx = Math.tan(radian);
        } else {
            model.tanx = Double.NaN;
        }

        if (-1.0 <= x && x <= 1.0) {
            model.asinx = Math.asin(x) * 180.0 / Math.PI;
            model.acosx = Math.acos(x) * 180.0 / Math.PI;
        } else {
            model.asinx = Double.NaN;
            model.acosx = Double.NaN;
        }

        // e为底的对数
        model.lnx = Math.log(x);
        // 10为底的对数
        model.log10x = Math.log10(x);
        // 2为底
        model.log2x = Math.log(x) / Math.log(2);

        if (x != 0) {
            model._1_d_x = 1 / x;
        } else {
            model._1_d_x = Double.NaN;
        }

        model.x___ = model.factorial(x);

        return model;
    }

    /** 计算x^y */
    public ScientificModel calculateX_Y(double x, double y) {

        this.x_y[0] = Math.pow(x, y);
        this.x_y[1] = y;

        return this;
    }

    /** 计算y^x */
    public ScientificModel calculateY_X(double x, double y) {

        this.y_x[0] = Math.pow(y, x);
        this.y_x[1] = y;

        return this;
    }

    /** logx(y) */
    public ScientificModel calculateLogxy(double x, double y) {

        this.logxy[0] = Math.log(y) / Math.log(x);
        this.logxy[1] = y;

        return this;
    }

    public double factorial(double x) {

        double result = 1;
        for (int i = 1; i <= (int)x; i++) {
            result = result * i;
        }

        return result;
    }

}
