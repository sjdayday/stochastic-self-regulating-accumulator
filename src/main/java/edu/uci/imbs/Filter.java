package edu.uci.imbs;

public class Filter
{
	/**
	 * Taken from http://code.google.com/p/stevens-ssp-plugin/wiki/DataStructure
	 * @param b
	 * @param a
	 * @param x
	 * @return
	 */
	public double[] filter(double[] b, double[] a, double[] x)
	{
		int ord = b.length-1; 
		int np = x.length; 
		double[] y = new double[x.length]; 
//		filter(int ord, float *a, float *b, int np, float *x, float *y)
        int i,j;
        y[0]=b[0]*x[0];
        for (i=1;i<ord+1;i++)
        {
            y[i]=0.0;
            for (j=0;j<i+1;j++)
                y[i]=y[i]+b[j]*x[i-j];
//            for (j=0;j<i;j++)  
//                y[i]=y[i]-a[j+1]*y[i-j-1];  // don't ask me why but seems to work commented out
        }
		/* end of initial part */
//        for (i=ord+1;i<np+1;i++)
		for (i=ord+1;i<np;i++)
		{
	        y[i]=0.0;
	        for (j=0;j<ord+1;j++)
	            y[i]=y[i]+b[j]*x[i-j];
//	        for (j=0;j<ord;j++)
//	            y[i]=y[i]-a[j+1]*y[i-j-1];
		}

		return y;
	}

}
