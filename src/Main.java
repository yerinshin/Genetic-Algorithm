import java.util.Random;

public class Main {
    public static int[] initA(){
        Random r = new Random();
        int[] a = new int[4];
        System.out.printf("a 후보해? : ");
        for (int i = 0; i < a.length; i++) {
            a[i] = r.nextInt(31+1);
            System.out.printf("%d", a[i]);
            System.out.printf(" ");
        }
        System.out.println();
        return a;
    } //a의 후보해 4개 선택 

    public static int[] initB() {
        Random r = new Random();
        int[] b = new int[4];
        System.out.printf("b 후보해? : ");
        for(int i=0; i<b.length; i++) {
            b[i] = r.nextInt(31+1);
            System.out.printf("%d", b[i]);
            System.out.printf(" ");
        }
        System.out.println();
        return b;
    } //b의 후보해 4개 선택

    public static int mse(int a, int b ,int[] x, int[] y){
        int gap;
        int yhat;
        int mse =0;

        for (int i = 0; i < x.length; i++) {
            yhat = a*x[i]+b;
            gap = y[i]-yhat;
            mse += gap * gap;
        }

        return mse;
    }

    public static int[] selection( int[] a, int[] b, int[] x, int[] y) {
        int[] f = new int[a.length]; //mse값을 담을 배열
        int sum=0; //mse의 합
        int[] fit = new int[a.length]; // (sum-f[i])를 담을 배열
        int fit_sum =0; // (sum-f[i])들의 합
        double[] ratio = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            f[i] = mse(a[i],b[i],x,y);
            sum += f[i];     //mse 더한 총 값
        }

        for (int i = 0; i < a.length; i++) {
            fit[i] = sum-f[i];             //mse가 작을 수록 큰 적합도를 가져야 하므로 새롭게 정의
        }

        for (int i = 0; i < a.length; i++) {
            fit_sum += fit[i]; //비율 계산 위해서 fit_sum값을 구함
        }

        for (int i = 0; i < ratio.length; i++) {
            if(i==0) ratio[i] = (double)(fit[i])/(double)fit_sum;
            else ratio[i] = ratio[i-1]+((double)(fit[i])/(double)fit_sum);
        }
        int[] s_result = new int[a.length*2];

        Random r = new Random();

        for (int i = 0; i < a.length; i++) {
            double p = r.nextInt();
            if(p<ratio[0]){
                s_result[i] = a[0];
                s_result[i+a.length] = b[0];
            }
            else if(p<ratio[1]){
                s_result[i] = a[1];
                s_result[i+a.length] = b[1];
            }
            else if(p<ratio[2]){
                s_result[i] = a[2];
                s_result[i+a.length] = b[2];
            }
            else {
                s_result[i] = a[3];
                s_result[i+a.length] = b[3];
            }
        }
        return s_result;
    }

    public static String int2String(String x) {
        return String.format("%8s", x).replace(' ', '0');
    }

    public static String[] crossover(int[] sx) {
        String[] c_result = new String[sx.length];
        for(int i=0; i<sx.length; i+=2) {
            String bit1 = int2String(Integer.toBinaryString(sx[i]));
            String bit2 = int2String(Integer.toBinaryString(sx[i+1]));

            c_result[i] = bit1.substring(0, 2) + bit2.substring(3);
            c_result[i+1] = bit2.substring(0, 2) + bit1.substring(3);
        }

        return c_result;
    }

    public static int invert(String mx) {
        Random r = new Random();
        int result = Integer.parseInt(mx, 2);
        for(int i=0; i<mx.length(); i++) {
            double p = (double)1/ (double)32; // (1/32)의 확률로 돌연변이가 된다.
            if(r.nextDouble() < p) {
                result = 1 << i ^ result;
            }
        }
        return result;
    }

    public static int[] mutation(String[] cx) {
        int[] m_result = new int[cx.length];
        for (int i=0; i<cx.length; i++) {
            m_result[i] = invert(cx[i]);
        }
        return m_result;
    }

    public static void main(String[] args) {
        int[] x = {11, 15,18, 22, 29, 32, 37, 43, 51, 59, 64, 66, 70};
        int[] y = {35, 38,49, 56, 66, 76, 89, 99, 112, 125, 144, 138, 152};

        int[] a = initA();
        int[] b = initB();

        int[] MSE = new int[a.length];
        double min = 10000.0;
        int grad=0;  //기울기
        int inter=0; //y절편

        for(int i=0; i<1000; i++) {
            int[] sx = selection(a,b,x,y);
            String[] cx = crossover(sx);
            int[] mx = mutation(cx);

            for (int j = 0; j <a.length ; j++) {
                a[j] = mx[j];
                b[j] = mx[j+a.length];
            }

            for(int j = 0; j <a.length; j++) {
                MSE[j] = mse(a[j],b[j],x,y);
                if(min>MSE[j]) {
                    min = MSE[j];
                    grad = a[j];
                    inter = b[j];
                }
            }
        }
        System.out.println("기울기 : " +grad);
        System.out.printf("y절편 : " +inter);
        System.out.printf("\n");
        System.out.printf("y = " +grad+"x +"+ inter);
    }
}