import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.Math;

public class cilindro extends JFrame implements KeyListener{

    private BufferedImage buff, buffer;
    private JPanel zonaTeclado;
    private JTextArea teclado;

    private int rM;

    public static Color rojoEspecial = new Color(236, 68, 92);

    private final int casillasVerticales = 30;
    private int casillasHorizontales = 30;

    public int xp = 0, yp = 300, zp = 100;

    int transformacionGeneral[][] = new int[8][4];
    int space[][][] = new int [casillasHorizontales][casillasVerticales][4];
    int curva[][] = new int[casillasVerticales][4];
    double[] repintarEscalacion = {.4,.4,.4};
    double[] repintarRotacion = {0, 0, 0};
    int[] repintarTraslacion = {250, 220, 0};
    int[] traslacionCurva = {200, 200, 0};

    public cilindro(){

        zonaTeclado = new JPanel();
        teclado = new JTextArea();
        teclado.addKeyListener(this);
        getContentPane().add(teclado, BorderLayout.CENTER);
        getContentPane().add(zonaTeclado, BorderLayout.CENTER);

        setTitle("Cilindro");
        setResizable(true);
		setSize(500,500);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buff = new BufferedImage(1,1, BufferedImage.TYPE_INT_RGB);

        paint();
    }

    public void putPixel(int x, int y, Color c){
        buff.setRGB(0, 0, c.getRGB());
        buffer.getGraphics().drawImage(buff, x, y, this);
    }

    public void puntoMedio(int x0, int y0, int x1, int y1){

        float x, y, dx, dy, incx = 1, incy = 1, incE, incNE, p = 0;

        x = x0;
        y = y0;

        dx = x1 - x0;
        dy = y1 - y0;

        if(dx < 0){
            dx = -dx;
            incx = -1;
        }
        if(dy < 0){
            dy = -dy;
            incy = -1;
        }

        if(Math.abs(dx) > Math.abs(dy)){ 
            incE = 2 * dy;
            incNE = 2 * (dy - dx);
            while(x != x1){
                putPixel((int)Math.round(x), (int)Math.round(y), rojoEspecial);
                x = x + incx;
                if(2 * (p + dy) < dx){
                    p = p + incE;
                }else{
                    p = p + incNE;
                    y = y + incy;
                }
            }
        }else{
            incE = 2 * dx;
            incNE = 2 * (dx - dy);
            while(y != y1){
                putPixel((int)Math.round(x), (int)Math.round(y), rojoEspecial);
                y = y + incy;
                if(2 * (p + dx) < dy){
                    p = p + incE;
                }else{
                    p = p + incNE;
                    x = x + incx;
                }
            }
        } 

    }


    public void formulaCurva(int curva[][]){
        double coorX, coorY;

        double maxX = 0, minX = 0;
        double maxY = 0, minY = 0;
        double maxZ = 0, minZ = 0;
        
        double t = 0;
        double exp = 2.3 * 4 / casillasVerticales;
        
        for (int i = 0; i < casillasVerticales; i++) {

            t = exp * i;

            coorX = ((Math.sin(t)) * 100/2.2) + 100;
            coorY = (-t * 100);
            
            curva[i][0] = (int)coorX;
            curva[i][1] = (int)coorY;
            curva[i][2] = 0;
            curva[i][3] = 1;

            if(i == 0){
                maxY = coorY;
                minY = coorY;
            }

            if (coorX < minX){
                minX = coorX;
            }
            if (coorX > maxX){
                maxX = coorX;
            }

            if (coorY < minY){
                minY = coorY;
            }
            if (coorY > maxY){
                maxY = coorY;
            }
        }

        double rangoX = maxX-minX;
        double rangoY = maxY-minY;
        double rangoZ = maxZ-minZ;


        if((rangoX) > (rangoY)){
            if((rangoX) > (rangoZ)){
                rM = (int)Math.round(rangoX);
            }
            else{
                rM = (int)Math.round(rangoZ);
            }
        }
        else if((rangoY) > (rangoZ)){
            rM = (int)Math.round(rangoY);
        }
        else{
            rM = (int)Math.round(rangoZ);
        }

        traslacionCurva[0] = rM;
        traslacionCurva[1] = rM;
        zp = rM;

        int temp[][] = {{0,0,0,1}};
        for (int i = 0; i < casillasHorizontales; i++) {
            for (int j = 0; j < casillasVerticales; j++){
                temp[0][0] = curva[j][0];
                temp[0][1] = curva[j][1] + rM/2;
                temp[0][2] = curva[j][2];
                temp[0][3] = curva[j][3];

                rot(temp, 0, 2 * Math.PI/casillasHorizontales * i, 0);

                space[i][j][0] = temp[0][0];
                space[i][j][1] = temp[0][1];
                space[i][j][2] = temp[0][2];
                space[i][j][3] = temp[0][3];

            }
        }
    }

    public void drawSup(int space[][][], int xp, int yp, int zp){
        int space2D[][][] = new int [casillasHorizontales][casillasVerticales][3];
        
        for (int i = 0; i < casillasHorizontales; i++) {
            for (int j = 0; j < casillasVerticales; j++){
                space2D[i][j] = calcular2D(space[i][j][0], space[i][j][1], space[i][j][2], xp,  yp, zp);
            }
        }

        for (int i = 1; i < casillasHorizontales; i++) {
            for (int j = 1; j < casillasVerticales; j++){
               
                puntoMedio(space2D[i-1][j-1][0], space2D[i-1][j-1][1], space2D[i-1][j][0], space2D[i-1][j][1]);
                puntoMedio(space2D[i-1][j-1][0], space2D[i-1][j-1][1], space2D[i][j-1][0], space2D[i][j-1][1]);
                //puntoMedio(space2D[i-1][j-1][0], space2D[i-1][j-1][1], space2D[i][j][0], space2D[i][j][1]);

                if(j == casillasVerticales - 1) {
                    puntoMedio(space2D[i-1][j][0], space2D[i-1][j][1], space2D[i][j][0], space2D[i][j][1]);
                }
                
                if(i == casillasHorizontales - 1){
                   
                    puntoMedio(space2D[i][j-1][0], space2D[i][j-1][1], space2D[i][j][0], space2D[i][j][1]);
                    puntoMedio(space2D[0][j-1][0], space2D[0][j-1][1], space2D[i][j-1][0], space2D[i][j-1][1]);

                    if(j == casillasVerticales - 1) {
                        puntoMedio(space2D[0][j][0], space2D[0][j][1], space2D[i][j][0], space2D[i][j][1]);
                    }
                }
            }
        }
    }

    
    public void paint(){

        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        formulaCurva(curva);
        
        for (int i=0; i<casillasHorizontales; i++){
            esc(space[i], repintarEscalacion[0], repintarEscalacion[1], repintarEscalacion[2]);
            rot(space[i], repintarRotacion[0], repintarRotacion[1], repintarRotacion[2]);
            tras(space[i], repintarTraslacion[0], repintarTraslacion[1], repintarTraslacion[2]);
        }

        drawSup(space, xp,  yp, zp);
        esc(transformacionGeneral, repintarEscalacion[0], repintarEscalacion[1], repintarEscalacion[2]);
        rot(transformacionGeneral, repintarRotacion[0], repintarRotacion[1], repintarRotacion[2]);
        tras(transformacionGeneral, repintarTraslacion[0], repintarTraslacion[1], repintarTraslacion[2]);

        zonaTeclado.getGraphics().drawImage(buffer, 0, 0, this);
    }

    public int[] calcular2D(int x1, int y1, int z1, int xp, int yp, int zp){  

        int x2, y2;

        x2 = x1 + ((xp * z1)/zp);
        y2 = y1 + ((yp * z1)/zp);

        int dot[] = {(int)x2, (int)y2};
        return dot;

    }

    public void esc(int [][] cubo, double Sx, double Sy, double Sz){

        for(int x1=0; x1<=cubo.length - 1; x1++){
            double r[]={0,0,0,0};
            double [] P = {cubo[x1][0], cubo[x1][1],cubo[x1][2], cubo[x1][3]};
            double [][] T = {
                {Sx,0,0,0},
                {0,Sy,0,0},
                {0,0,Sz,0},
                {0,0,0,1}
            };
            int i,j;
            for(i=0;i<4;i++){
                for(j=0;j<4;j++){
                    r[i] += P[j]*T[i][j];
                }
            }
            cubo[x1][0]=(int)r[0];
            cubo[x1][1]=(int)r[1];
            cubo[x1][2]=(int)r[2];
            cubo[x1][3]=(int)r[3];
        }

    }

    public void rot(int [][] cubo, double Ax, double Ay, double Az){
    
        for(int x1=0; x1<=cubo.length - 1; x1++){
            double r[]={0,0,0,0};
            double [] P = {cubo[x1][0], cubo[x1][1],cubo[x1][2], cubo[x1][3]};
            double [][] T = {
                {Math.cos(Ax),-Math.sin(Ax),0,0},
                {Math.sin(Ax),Math.cos(Ax),0,0},
                {0,0,1,0},
                {0,0,0,1}
            };
            int i,j;
            for(i=0;i<4;i++){
                for(j=0;j<4;j++){
                    r[i] += P[j]*T[i][j];
                }
            }
            cubo[x1][0]=(int)r[0];
            cubo[x1][1]=(int)r[1];
            cubo[x1][2]=(int)r[2];
            cubo[x1][3]=(int)r[3];
        }
        
        for(int x1=0; x1<=cubo.length - 1; x1++){
            double r[]={0,0,0,0};
            double [] P = {cubo[x1][0], cubo[x1][1],cubo[x1][2], cubo[x1][3]};
            double [][] T = {
                {Math.cos(Ay), 0, Math.sin(Ay), 0},
                {0, 1, 0, 0},
                {-Math.sin(Ay), 0, Math.cos(Ay), 0},
                {0, 0, 0, 1}
            };
            int i,j;
            for(i=0;i<4;i++){
                for(j=0;j<4;j++){
                    r[i] += P[j]*T[i][j];
                }
            }
            cubo[x1][0]=(int)r[0];
            cubo[x1][1]=(int)r[1];
            cubo[x1][2]=(int)r[2];
            cubo[x1][3]=(int)r[3];
        }

        for(int x1=0; x1<=cubo.length - 1; x1++){
            double r[]={0,0,0,0};
            double [] P = {cubo[x1][0], cubo[x1][1],cubo[x1][2], cubo[x1][3]};
            double [][] T = {
                {1,0,0,0},
                {0,Math.cos(Az),-Math.sin(Az),0},
                {0,Math.sin(Az),Math.cos(Az),0},
                {0,0,0,1}
            };
            int i,j;
            for(i=0;i<4;i++){
                for(j=0;j<4;j++){
                    r[i] += P[j]*T[i][j];
                }
            }
            cubo[x1][0]=(int)r[0];
            cubo[x1][1]=(int)r[1];
            cubo[x1][2]=(int)r[2];
            cubo[x1][3]=(int)r[3];
        }

    }

    public void tras(int [][] cubo, int dx,int dy, int dz){

        for(int x1=0; x1<=cubo.length - 1; x1++){
            int r[]={0,0,0,0};
            int [] P = {cubo[x1][0], cubo[x1][1],cubo[x1][2], cubo[x1][3]};
            int [][] T = {{1,0,0,dx},
                          {0,1,0,dy},
                          {0,0,1,dz},
                          {0,0,0,1}};
            int i,j;
            for(i=0;i<4;i++){
                for(j=0;j<4;j++){
                    r[i] += P[j]*T[i][j];
                }
            }
            cubo[x1][0]=r[0];
            cubo[x1][1]=r[1];
            cubo[x1][2]=r[2];
            cubo[x1][3]=r[3];
        }

    }

    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            repintarRotacion[0] = repintarRotacion[0] - Math.PI/32;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            repintarRotacion[0] = repintarRotacion[0] + Math.PI/32;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            repintarRotacion[1] = repintarRotacion[1] + Math.PI/8;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            repintarRotacion[1] = repintarRotacion[1] - Math.PI/8;
        }
        if (e.getKeyCode() == KeyEvent.VK_K) {
            repintarRotacion[2] = repintarRotacion[2] - Math.PI/32;
        }
        if (e.getKeyCode() == KeyEvent.VK_L) {
            repintarRotacion[2] = repintarRotacion[2] + Math.PI/32;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_A) {
            repintarTraslacion[0] = repintarTraslacion[0] - 50;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            repintarTraslacion[0] = repintarTraslacion[0] + 50;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            repintarTraslacion[1] = repintarTraslacion[1] - 50;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            repintarTraslacion[1] = repintarTraslacion[1] + 50;
        }

        if (e.getKeyCode() == KeyEvent.VK_M) {
            repintarEscalacion[0] = repintarEscalacion[0] + 0.1;
            repintarEscalacion[1] = repintarEscalacion[1] + 0.1;
            repintarEscalacion[2] = repintarEscalacion[2] + 0.1;
        }
        if (e.getKeyCode() == KeyEvent.VK_N) {
            repintarEscalacion[0] = repintarEscalacion[0] - 0.1;
            repintarEscalacion[1] = repintarEscalacion[1] - 0.1;
            repintarEscalacion[2] = repintarEscalacion[2] - 0.1;
        }
        
        paint();
    }

    public static void main(String[] args) {
        cilindro b = new cilindro();
    }
}
