import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;  
import java.util.ArrayList;

public class MazeProgram extends JPanel implements KeyListener {

    private JFrame frame;
    private int x, y, mx, my, origX, origY, origMX, origMY;
    private int baseX = 30;
    private int baseY = 30;
    private int dim = 15;
    private int playerWidth = 10;
    private int playerHeight = 10;
    private int blockWidth = 5;
    private int blockHeight = 5;
    int count;
    private int dir = 0;
    private boolean in3d = false;
    private int dist = 50;
    private Color color;
    private String[][] maze;
    private ArrayList<Wall> walls;
    BufferedImage rightStand, rightWalk, monst;
    BufferedImage[] mons3D=new BufferedImage[3];
    private int WIDTH = 800;
    private int HEIGHT = 1200;

    public MazeProgram() {

        frame = new JFrame();
        frame.add(this);
        x = 0; y = 0;
        frame.addKeyListener(this);
        frame.setSize(WIDTH, HEIGHT);

        try{
         rightWalk=ImageIO.read(new File("rightWalk.png"));
         rightStand=ImageIO.read(new File("rightStand.png"));
         monst=ImageIO.read(new File("monst.png"));
        }
        catch(IOException e){}

        rightWalk=resize(rightWalk, dim, dim);
        rightStand=resize(rightStand, dim, dim);
        monst=resize(monst, dim, dim);
      
        for(int x=0;x<5;x++)
        {
            mons3D[x]=resize(monst,500-x*75,500-x*75);
        } 
		setMaze();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
    public BufferedImage resize(BufferedImage image, int width, int height)
    {
        Image temp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage scaledVersion = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaledVersion.createGraphics();
        g2.drawImage(temp, 0, 0, null);
        g2.dispose();
        return scaledVersion;
    }

    public class Wall {

        private int[] x;
        private int[] y;
        private int dist;
        private Color color;
        private int colNum;
        private String type;

        public Wall(int[] x, int[] y, int dist, Color color, int colNum, String type) {

            this.x = x;
            this.y = y;
            this.color = color;
            this.dist = dist;
            this.colNum = colNum;
            this.type = type;
        }

        public Polygon getWall() {
            return new Polygon(x, y, x.length);
        }

        public String getType() {
            return this.type;
        }


        public Color getColor() {
            return color;
        }

        public GradientPaint getPaint() {

            Color startCol = new Color(colNum, colNum, colNum);
            Color endCol = new Color(colNum - dist, colNum - dist, colNum - dist);

            if (type.equals("LeftRight")) {
                return new GradientPaint(x[1], y[0], startCol, x[1], y[0], endCol); 

            }

            if (type.equals("Top") || type.equals("Ceiling")) {
                return new GradientPaint(x[0], y[0], startCol, x[0], y[1], endCol);
            }

            if (type.equals("Floor")) {
                return new GradientPaint(x[0], y[0], startCol, x[0], y[1], endCol);
            }       

            if (type.equals("LeftPathway") || type.equals("RightPathway")) {
                return new GradientPaint(x[1], y[1], startCol, x[1], y[1], endCol);

            }
            return new GradientPaint(x[0], y[1], startCol, x[1], y[0], endCol);
        }
    }

    public void setMaze() {

        try {

            File f = new File("maze1.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));

            String line;
            count = 0;
            maze = new String[41][];


            while ((line = br.readLine()) != null) {

                maze[count] = line.split("");

                if (line.indexOf("N") > 0) {

                    x = line.indexOf("N");
                    y = count;
                    origX=x;
                    origY=y;
                    dir = 0;

                }

                if (line.indexOf("E") > 0) {

                    x = line.indexOf("E");
                    y = count;
                    origX=x;
                    origY=y;
                    dir = 1;
                }


                if (line.indexOf("S") > 0) {
                    x = line.indexOf("S");
                    y = count;
                    origX=x;
                    origY=y;
                    dir = 2;
                }

                if (line.indexOf("W") > 0) {

                    x = line.indexOf("W");
                    y = count;
                    origX=x;
                    origY=y;
                    dir = 3;
                }

                count++;
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        g2.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);

        if(maze == null) 
            return;

        if(!in3d){
            for (int i = 0; i < maze.length; i++) {

                for (int j = 0; j < maze[i].length; j++) {

                    if (maze[i][j].equals("#")) {

                        g.setColor(Color.GREEN);
                        g.drawRect(j * dim + baseX, i * dim + baseY, dim, dim);

                    }
                }
            }
            g.setColor(new Color(000,000,111));
            g.setColor(Color.CYAN);
            if(count%2==0)
            g.drawImage(rightStand,x*dim+40,y*dim+40,dim,dim, this);
            else       
            g.drawImage(rightWalk,x*dim+40,y*dim+40,dim,dim, this);          
            g.setColor(new Color(111,000,000));
            g.setColor(Color.RED);    
            g.drawImage(monst,mx*dim+40,my*dim+40,dim,dim, this);
        } 
        else {

            for (Wall wall : walls) {

                if (wall.getType().equals("")) {

                    g2.setColor(wall.getColor());
                } else {
                    g2.setPaint(wall.getPaint());
                }
                g2.fill(wall.getWall());
            }
        }
    }

    @Override

    public void keyPressed(KeyEvent e) {

        count++;
        e.getKeyCode();

        if (e.getKeyCode() == 38) {

            try {

                switch (dir) {

                    case 0:

                        if (!maze[y - 1][x].equals("#"))

                            this.y -= 1;

                        break;
                    case 1:

                        if (!maze[y][x + 1].equals("#"))

                            this.x += 1;

                        break;

                    case 2:

                        if (!maze[y + 1][x].equals("#"))

                            this.y += 1;

                        break;

                    case 3:

                        if (!maze[y][x - 1].equals("#"))

                            this.x -= 1;

                        break;
                }

            } catch (ArrayIndexOutOfBoundsException ex) {

                System.out.println("Out of bounds");

            }
        }

        if (e.getKeyCode() == 37) {
            dir--;
            if (dir < 0)
                dir = 3;
        }

        if (e.getKeyCode() == 39) {
            dir++;
            dir %= 4;
        }

        if (e.getKeyCode() == 32) {
            in3d = !in3d;
        }

        if (in3d) {

            walls = new ArrayList<>();
            addLeftPathways();
            addLeftWalls();
            addRightPathways();
            addRightWalls();
            addFloors();
            addTopWalls();
            addFrontWalls();
        }
        repaint();
    }
    public void addLeftPathways() {

        for (int a = 0; a < 5; a++) {

            try {

                int[] xx = {100 + dist * a, 150 + dist * a, 150 + dist * a, 100 + dist * a};
                int[] yy = {100 + dist * a, 150 + dist * a, 650 - dist * a, 700 - dist * a};

                walls.add(new Wall(xx, yy, 50, Color.RED, 255-dist*a, "LeftPathway"));

            } catch (ArrayIndexOutOfBoundsException e){}
        }
    }

    public void addRightPathways()
    {
        for (int a = 0; a < 5; a++) {

            try {

                int[] xx = {700 - dist * a, 650 - dist * a, 650 - dist * a, 700 - dist * a};
                int[] yy = {100 + dist * a, 150 + dist * a, 650 - dist * a, 700 - dist * a};

                walls.add(new Wall(xx, yy, 50, Color.RED, 255-dist*a, "RightPathway"));

            } catch (ArrayIndexOutOfBoundsException e){}
        }
    }

    public void addFrontWalls() {

        for (int a = 3; a > 0; a--) {

            try {

                int[] xx = {100+dist*a, 700-dist*a, 700-dist*a, 100+dist*a};
                int[] yy = {100+dist*a, 100+dist*a, 700-dist*a, 700-dist*a};

                if (dir == 0 && maze[y - a][x].equals("#")) 

                {
                    walls.add(new Wall(xx, yy, 50, Color.RED, 255 - dist * a, "FrontWall"));
                }

                if (dir == 1 && maze[y][x + a].equals("#")) 
                {
                    walls.add(new Wall(xx, yy, 50, Color.RED, 255 - dist * a, "FrontWall"));
                }

                if (dir == 2 && maze[y + a][x].equals("#")) 

                {
                    walls.add(new Wall(xx, yy, 50, Color.RED, 255 - dist * a, "FrontWall"));
                }

                if (dir == 3 && maze[y][x - a].equals("#")) 
                {
                    walls.add(new Wall(xx, yy, 50, Color.GREEN, 255 - dist * a, "FrontWall"));
                }

            } catch (ArrayIndexOutOfBoundsException e) {}
        }
    }

    public void addFloors() {
        for (int a = 0; a < 5; a++) {

            int[] xx = {100 + dist * a, 150 + dist * a, 650 - dist * a, 700 - dist * a};
            int[] yy = {700 - dist * a, 650 - dist * a, 650 - dist * a, 700 - dist * a};

            walls.add(new Wall(xx, yy, 50, Color.RED, 255 - dist * a, "Floor"));
        }
    }

    public void addTopWalls() {

        for (int a = 0; a < 5; a++) {

            int[] xx = {100 + dist * a, 150 + dist * a, 150 + dist * a, 100 + dist * a};
            int[] yy = {100 + dist * a, 150 + dist * a, 650 - dist * a, 700 - dist * a};

            walls.add(new Wall(yy, xx, 50, Color.RED, 255 - dist * a, "Top"));  
        }
    }
    
    public void addLeftWalls() {

        for (int a = 0; a < 5; a++) {

            try {

                int[] xx = {100 + dist * a, 150 + dist * a, 150 + dist * a, 100 + dist * a};
                int[] yy = {100 + dist * a, 150 + dist * a, 650 - dist * a, 700 - dist * a};

                if (dir == 0 && maze[y - a][x - 1].equals("#")) {

                    walls.add(new Wall(xx, yy, 50, Color.YELLOW, 255 - dist * a, "LeftRight"));
                }
                if (dir == 1 && maze[y - 1][x + a].equals("#")) {
                    walls.add(new Wall(xx, yy, 50, Color.YELLOW, 255 - dist * a, "LeftRight"));
                }
                if (dir == 2 && maze[y + a][x + 1].equals("#")) {
                    walls.add(new Wall(xx, yy, 50, Color.YELLOW, 255 - dist * a, "LeftRight"));
                }

                if (dir == 3 && maze[y + 1][x - a].equals("#")) {
                    walls.add(new Wall(xx, yy, 50, Color.YELLOW, 255 - dist * a, "LeftRight"));
                }

            } catch (ArrayIndexOutOfBoundsException e) {}
        }
    }

    public void addRightWalls() {

        for (int a = 0; a < 5; a++) {

            try {

                int[] xx = {700 - dist * a, 650 - dist * a, 650 - dist * a, 700 - dist * a};
                int[] yy = {100 + dist * a, 150 + dist * a, 650 - dist * a, 700 - dist * a};

                if (dir == 0 && maze[y - a][x + 1].equals("#")) {
                    walls.add(new Wall(xx, yy, 50, Color.YELLOW, 255 - dist * a, "LeftRight"));
                }

                if (dir == 1 && maze[y + 1][x + a].equals("#")) {
                    walls.add(new Wall(xx, yy, 50, Color.YELLOW, 255 - dist * a, "LeftRight"));
                }

                if (dir == 2 && maze[y + a][x - 1].equals("#")) {
                    walls.add(new Wall(xx, yy, 50, Color.YELLOW, 255 - dist * a, "LeftRight"));
                }

                if (dir == 3 && maze[y - 1][x - a].equals("#")) {

                    walls.add(new Wall(xx, yy, 50, Color.YELLOW, 255 - dist * a, "LeftRight"));
                }

            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }
    }
    @Override

    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
    public static void main(String[] args)
	{
		MazeProgram app = new MazeProgram();
	}
}