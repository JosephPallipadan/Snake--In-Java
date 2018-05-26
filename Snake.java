
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Snake extends JPanel {

    final static int UP=1, DOWN=2, LEFT=3, RIGHT=4;

    boolean gameStarted=false, gameOver=false;
    Timer gameTimer;
    Timer speedAdjuster;
    JTextField speed=new JTextField("1");

    ArrayList<SnakeBody> partList=new ArrayList<SnakeBody>();
    ArrayList<Position> turnList=new ArrayList<Position>();
    Fruit fruit=new Fruit();

    static ActionListener listener;
    static ActionListener speedTimer;
    static int score=0;
    static int keyVar, timerVar=0;
    static long time;
    static double endTime;
    static double speedAsNumber=100;

    public Snake()
    {

        setLayout(null);

        add(speed);
        speed.setBounds(315, 400, 100, 20);
        speed.setName("Speed");

        partList.add(new SnakeBody(2, 0, true, false));
        partList.add(new SnakeBody(1, 0, false, false));
        partList.add(new SnakeBody(0, 0, false, true));

        addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent evt)
            {
                if(timerVar>keyVar)
                {
                    keyVar=timerVar;
                    if(evt.getKeyCode()==KeyEvent.VK_UP && partList.get(0).dir!=DOWN)
                    {
                        partList.get(0).dir=UP;
                        turnList.add(new Position(partList.get(0).posx, partList.get(0).posy, UP));
                    }

                    else if(evt.getKeyCode()==KeyEvent.VK_DOWN && partList.get(0).dir!=UP)
                    {
                        partList.get(0).dir=DOWN;
                        turnList.add(new Position(partList.get(0).posx, partList.get(0).posy, DOWN));
                    }

                    else if(evt.getKeyCode()==KeyEvent.VK_LEFT && partList.get(0).dir!=RIGHT)
                    {
                        partList.get(0).dir=LEFT;
                        turnList.add(new Position(partList.get(0).posx, partList.get(0).posy, LEFT));
                    }

                    else if(evt.getKeyCode()==KeyEvent.VK_RIGHT && partList.get(0).dir!=LEFT)
                    {
                        partList.get(0).dir=RIGHT;
                        turnList.add(new Position(partList.get(0).posx, partList.get(0).posy, RIGHT));
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent evt)
            {
                if(gameStarted && gameOver)
                {
                    gameOver=false;

                    partList=new ArrayList<SnakeBody>();
                    turnList=new ArrayList<Position>();

                    partList.add(new SnakeBody(2, 0, true, false));
                    partList.add(new SnakeBody(1, 0, false, false));
                    partList.add(new SnakeBody(0, 0, false, true));

                    score=0;
                    speedAsNumber=(Double.parseDouble(speed.getText())*100);
                    gameTimer=new Timer((int)speedAsNumber, listener);
                    speed.resize(0, 0);
                    time=System.currentTimeMillis();

                    gameTimer.start();
                }

                else if(!gameStarted && !gameOver)
                {
                    gameStarted=true;
                    speedAsNumber=(Double.parseDouble(speed.getText())*100);
                    gameTimer=new Timer((int)speedAsNumber, listener);
                    gameTimer.start();
                    speed.resize(0, 0);
                    time=System.currentTimeMillis();
                }

                repaint();

            }
        });



        listener= evt -> {
            timerVar++;


            /** Adding a part if player ate a fruit **/

            if(partList.get(0).posx==fruit.x && partList.get(0).posy==fruit.y)
            {
                speedAsNumber/=score;

                partList.get(partList.size()-1).tail=false;
                fruit.setPos();
                score++;

                switch(partList.get(partList.size()-1).dir)
                {
                    case LEFT :
                        partList.add( new SnakeBody(partList.get(partList.size()-1).posx+1, partList.get(partList.size()-1).posy, false, true) );
                        partList.get(partList.size()-1).dir=LEFT;
                        break;

                    case RIGHT :
                        partList.add( new SnakeBody(partList.get(partList.size()-1).posx-1, partList.get(partList.size()-1).posy, false, true) );
                        partList.get(partList.size()-1).dir=RIGHT;
                        break;

                    case UP :
                        partList.add( new SnakeBody(partList.get(partList.size()-1).posx, partList.get(partList.size()-1).posy+1, false, true) );
                        partList.get(partList.size()-1).dir=UP;
                        break;

                    case DOWN :
                        partList.add( new SnakeBody(partList.get(partList.size()-1).posx, partList.get(partList.size()-1).posy-1, false, true) );
                        partList.get(partList.size()-1).dir=DOWN;
                }

            }

            /** Algorithm for turning **/

            if(turnList.size()>0)
            {
                for(int x=0; x<turnList.size(); x++)
                {
                    for(int y=0; y<partList.size(); y++)
                    {
                        if(partList.get(y).posx==turnList.get(x).x && partList.get(y).posy==turnList.get(x).y)
                            partList.get(y).dir=turnList.get(x).dir;

                        if(partList.get(y).posx==turnList.get(x).x && partList.get(y).posy==turnList.get(x).y && partList.get(y).tail)
                        {
                            turnList.remove(x);
                            if(turnList.size()==0)
                                break;
                            x--;
                        }
                    }

                    if(turnList.size()==0)
                        break;
                }
            }

            /** Algorithm for changing the position **/

            for(int x=0; x<partList.size(); x++)
            {
                if(partList.get(x).dir==RIGHT)
                    partList.get(x).posx++;

                else if(partList.get(x).dir==LEFT)
                    partList.get(x).posx--;

                else if(partList.get(x).dir==UP)
                    partList.get(x).posy--;

                else if(partList.get(x).dir==DOWN)
                    partList.get(x).posy++;
            }

            repaint();

            /** Checking if player hit a wall **/


            if(partList.get(0).posx==26 || partList.get(0).posy==25 || partList.get(0).posx==-1 || partList.get(0).posy==-1)
            {
                gameOver=true;
                gameTimer.stop();
                repaint();
            }


            /**  Checking if player hit himself **/

            for(int x=1; x<partList.size(); x++)
            {
                if(partList.get(x).posx == partList.get(0).posx && partList.get(x).posy == partList.get(0).posy)
                {
                    gameOver=true;
                    gameTimer.stop();
                }

            }

        };

        speedTimer=new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                speedAsNumber=(Double.parseDouble(speed.getText())*100);
                //gameTimer=new Timer((int)speedAsNumber, listener);
            }
        };

        speedAdjuster=new Timer(100, speedTimer);
        speedAdjuster.start();
    }

    public void paintComponent(Graphics g)
    {

        this.requestFocus();


        if(gameStarted && gameOver)
        {
            endTime=(System.currentTimeMillis()-time)/1000.0;
            DecimalFormat df=new DecimalFormat("0.000");
            String a=df.format(endTime);

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 520, 520);

            g.setColor(Color.RED);
            g.setFont(new Font("Papyrus", Font.BOLD, 40));
            g.drawString("Game Over", 140, 200);
            g.drawString("Click To Play Again", 50, 320);
            g.drawString("Score : "+score, 160, 100);

            g.setFont(new Font("Papyrus", Font.BOLD, 20));

            g.drawString("( "+a+" Seconds )", 300, 20);
            g.drawString("Set Speed (0.01-10) : ", 95, 415);

            speed.resize(100, 20);
        }

        else if(!gameStarted && !gameOver)
        {
            g.setColor(Color.RED);
            g.fillRect(0, 0, 520, 520);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Papyrus", Font.BOLD, 40));
            g.drawString("Click To Start", 100, 260);

            g.setFont(new Font("Papyrus", Font.BOLD, 20));
            g.drawString("Set Speed (0.01-10) : ", 95, 415);
        }

        else
        {
            g.setColor(Color.RED);
            g.fillRect(0, 0, 520, 520);


            g.setColor(Color.BLUE);

            for(int x=20; x<520; x+=20)
                g.drawLine(x, 0, x, 520);
            for(int y=20; y<520; y+=20)
                g.drawLine(0, y, 520, y);

            g.setColor(Color.BLACK);
            for(int x=0; x<partList.size(); x++)
            {

                g.fillRect(partList.get(x).posx*20, partList.get(x).posy*20, 20, 20);

                if(partList.get(x).head)
                {
                    g.setColor(Color.RED);
                    g.fillOval(partList.get(x).posx*20+5, partList.get(x).posy*20+5, 10, 10);
                    g.setColor(Color.BLACK);
                }

            }

            g.setColor(Color.GREEN);
            g.fillRect(fruit.x*20, fruit.y*20, 20, 20);


        }
    }

    public static void main(String[] args)
    {
        JFrame Window=new JFrame("Snake");
        Window.setContentPane(new Snake());
        //Window.pack();
        Window.setVisible(true);
    }

    class SnakeBody
    {
        int posx=0, posy=0;
        boolean head, tail;
        int dir=RIGHT;

        SnakeBody(int x, int y, boolean head, boolean tail)
        {
            posx=x;
            posy=y;
            this.head=head;
            this.tail=tail;
        }

    }

    class Position
    {
        int x,y,dir;

        Position(int x, int y, int dir)
        {
            this.x=x;
            this.y=y;
            this.dir=dir;
        }
    }

    class Fruit
    {
        int x,y;

        Fruit()
        {
            x=(int)(Math.random()*23+3);
            y=(int)(Math.random()*25+1);
        }

        void setPos()
        {
            x=(int)(Math.random()*24+1);
            y=(int)(Math.random()*24+1);

            while(isInSnake(x, y))
            {
                x=(int)(Math.random()*24+1);
                y=(int)(Math.random()*24+1);
            }
        }

        boolean isInSnake(int x, int y)
        {
            for(int i=0; i<partList.size(); i++)
            {
                if(partList.get(i).posx==x && partList.get(i).posy==y)
                    return true;
            }

            return false;
        }
    }


}

