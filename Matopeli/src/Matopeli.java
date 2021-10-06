


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @author ville
 */
public class Matopeli extends Application {
    //muuttujat
    
    static int nopeus = 5;
    static int ruoanvari = 0;
    static int leveys = 25;
    static int korkeus = 25;
    static int ruokaX = 0;
    static int ruokaY = 0;
    static int matoLaatikko = 30;
    static List<Laatikko> mato = new ArrayList<>();
    static Dir suunta = Dir.vasen;
    static boolean gameOver = false;
    static Random rand = new Random();
    
    public enum Dir
    {
        vasen, oikea, ylos, alas
    }
    
    public static class Laatikko
    {
        int x;
        int y;
        
        public Laatikko(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        uusiRuoka();
        
        VBox root = new VBox();
        Canvas c = new Canvas(leveys*matoLaatikko, korkeus*matoLaatikko);
        GraphicsContext gc =c.getGraphicsContext2D();
        root.getChildren().add(c);
        
        new AnimationTimer()
        {
            long lastTick = 0;
            
            public void handle(long now)
            {
                if (lastTick == 0)
                {
                    lastTick = now;
                    tick(gc);
                    return;
                }
                
                if (now - lastTick > 1000000000/nopeus)
                {
                    lastTick = now;
                    tick(gc);
                }
            }
        }.start();
        
        Scene scene = new Scene(root, leveys*matoLaatikko, korkeus*matoLaatikko);
        
        //kontrollit
        scene.addEventFilter(KeyEvent.KEY_PRESSED,key->
        {
            if(key.getCode() == KeyCode.W)
            {
                suunta = Dir.ylos;
            }
            if(key.getCode() == KeyCode.S)
            {
                suunta = Dir.alas;
            }
            if(key.getCode() == KeyCode.A)
            {
                suunta = Dir.vasen;
            }
            if(key.getCode() == KeyCode.D)
            {
                suunta = Dir.oikea;
            }            
        });
        
        
        //Aloitus mato
        mato.add(new Laatikko(leveys / 2, korkeus / 2));
        mato.add(new Laatikko(leveys / 2, korkeus / 2));
        mato.add(new Laatikko(leveys / 2, korkeus / 2));
        
        primaryStage.setTitle("MATOPELI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
public static void tick(GraphicsContext gc)
{
    if (gameOver)
    {
        gc.setFill(Color.RED);
        gc.setFont(new Font("", 75));
        gc.fillText("GAME OVER", 150, 350);
        return;
    }
    
    for (int i = mato.size() - 1; i >=1; i--)
    {
        mato.get(i).x = mato.get(i-1).x;
        mato.get(i).y = mato.get(i-1).y;
    }
    
    switch(suunta)
    {
        case ylos:
            {
                mato.get(0).y--;
                if(mato.get(0).y < 0)
                {
                    gameOver = true;
                }
                break;
            }
        case alas:
            {
                mato.get(0).y++;
                if(mato.get(0).y > korkeus)
                {
                    gameOver = true;
                }
                break;
            }
        case vasen:
            {
                mato.get(0).x--;
                if(mato.get(0).x < 0)
                {
                    gameOver = true;
                }
                break;
            }
        case oikea:
            {
                mato.get(0).x++;
                if(mato.get(0).x > leveys)
                {
                    gameOver = true;
                }
                break;
            }
    }
    
    //syö ruoka
    if(ruokaX == mato.get(0).x && ruokaY == mato.get(0).y)
    {
        mato.add(new Laatikko(-1,-1));
        uusiRuoka();
    }
    
    //syö itseä
    for(int i=1; i < mato.size(); i++)
    {
        if(mato.get(0).x == mato.get(i).x && mato.get(0).y == mato.get(i).y)
        {
            gameOver = true;
        }
    }
    
    //reuna
    gc.setFill(Color.RED);
    gc.fillRect(0, 0, leveys * matoLaatikko, korkeus * matoLaatikko);
    
    //tausta
    gc.setFill(Color.GREEN);
    gc.fillRect(2, 2, leveys * matoLaatikko - 4, korkeus * matoLaatikko - 4);
    
    //Pisteet
    gc.setFill(Color.BLACK);
    gc.setFont(new Font("", 40));
    gc.fillText("Pisteet: " +(nopeus-6), 40, 75);
    
    //random ruoan väri
    Color cc = Color.WHITE;
    switch (ruoanvari)
    {
        case 0: cc = Color.PURPLE;
        break;
        case 1: cc = Color.ORANGE;
        break;
        case 2: cc = Color.RED;
        break;
        case 3: cc = Color.PINK;
        break;
        case 4: cc = Color.YELLOW;
        break;
    }
    gc.setFill(cc);
    gc.fillOval(ruokaX * matoLaatikko, ruokaY * matoLaatikko, matoLaatikko, matoLaatikko);
    
    //mato
    for(Laatikko c: mato)
    {
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(c.x * matoLaatikko, c.y * matoLaatikko, matoLaatikko -1, matoLaatikko -1);
        gc.setFill(Color.BLACK);
        gc.fillRect(c.x * matoLaatikko, c.y * matoLaatikko, matoLaatikko -2, matoLaatikko -2);
        
    }
    
}
    
//ruoka
    public static void uusiRuoka()
    {
        start:while(true)
        {
            ruokaX = rand.nextInt(leveys);
            ruokaY = rand.nextInt(korkeus);
            
            for(Laatikko c : mato)
            {
                if(c.x == ruokaX && c.y == ruokaY)
                {
                    continue start;
                }
            }
            ruoanvari = rand.nextInt(5);
            nopeus++;
            break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
