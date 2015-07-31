import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Write a description of class Section here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Section
{
    private int mouseX, mouseY;
    private boolean confilct;
    private ArrayList<Tile> tiles;
    private ArrayList<Character> characters;
    private Tile currentTile;
    private Character user;

    public Section(boolean firstSection) {
        mouseX = 0;
        mouseY = 0;
        confilct = false;

        tiles = new ArrayList<Tile>();
        int i = 1;
        for(int row = 0; row < 15; row++)
            for(int col = 0; col < 15; col++) {
                Color tileColor = new Color((int)(Math.random() * 156), (int)(Math.random() * 156 + 100), (int)(Math.random() * 156));
                if(i == 8)
                    tiles.add(new NextSectionTile("up", tileColor, i++, col * 40, row * 40));
                else if (i == 106) {
                    tiles.add(new NextSectionTile("left", tileColor, i++, col * 40, row * 40));
                } else if (i == 120) {
                    tiles.add(new NextSectionTile("right", tileColor, i++, col * 40, row * 40));
                } else if (i == 218) {
                    tiles.add(new NextSectionTile("down", tileColor, i++, col * 40, row * 40));
                } else {
                    tiles.add(new Tile(tileColor, i++, col * 40, row * 40));
                }
            }

        characters = new ArrayList<Character>();

        if(firstSection)
            addCharacter(new Character("You", 280, 280), 280, 280);
    }

    public void draw(Graphics2D g2) {
        if(!confilct) {
            for(Tile t : tiles) {
                int xDifference = mouseX - t.getX();
                int yDifference = mouseY - t.getY();
                if(xDifference < 40 && xDifference >= 0 && yDifference < 40 && yDifference >= 0) {
                    currentTile = t;
                    t.highlight(true);
                } else
                    t.highlight(false);
                t.assignDistanceValue(1000);
            }

            calculatePath(user.getTile(), currentTile);
            for(Tile t : tiles)
                t.draw(g2);
            for(Character c : characters)
                c.draw(g2);
        } else {

        }
    }

    public void addCharacter(Character character, int x, int y) {
        characters.add(character);
        if(character.getName().equals("You"))
            user = character;
        for(Tile t : tiles)
            if(Math.abs(x - t.getX()) < 40 && Math.abs(y - t.getY()) < 40) {
                ArrayList<Tile> startingPath = new ArrayList<Tile>();
                startingPath.add(t);
                character.setPath(startingPath);
                character.setCurrentTile(t);
                t.addCharacter(character);
                break;
            }
    }

    public void updateInfo(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void click() {
        user.setPath(calculatePath(user.getTile(), currentTile));
    }

    public ArrayList<Tile> calculatePath(Tile from, Tile to) {
        ArrayList<Tile> path = new ArrayList<Tile>();
        assignDistanceValues(0, to);
        int toX = to.getX();
        int toY = to.getY();

        Tile nextTile = from;
        while(nextTile != to) {
            int x = nextTile.getX();
            int y = nextTile.getY();
            ArrayList<Tile> possiblePath = new ArrayList<Tile>();
            if(x > 0)
                possiblePath.add(leftOf(nextTile));
            if(x < 560)
                possiblePath.add(rightOf(nextTile));
            if(y > 0)
                possiblePath.add(above(nextTile));
            if(y < 560)
                possiblePath.add(below(nextTile));

            int shortestDistance = nextTile.getDistanceValue();
            for(Tile t : possiblePath) {
                int tileDistance = t.getDistanceValue();
                if(tileDistance <= shortestDistance && Math.sqrt(Math.pow(toX - t.getX(), 2) + Math.pow(toY - t.getY(), 2)) < Math.sqrt(Math.pow(toX - nextTile.getX(), 2) + Math.pow(toY - nextTile.getY(), 2))) {
                    nextTile = t;
                    shortestDistance = tileDistance;
                }
            }

            path.add(nextTile);
            nextTile.highlight(true);
        }
        return path;
    }

    public void assignDistanceValues(int i, Tile to) {
        to.assignDistanceValue(i);
        int x = to.getX();
        int y = to.getY();
        if(x > 0) {
            Tile left = leftOf(to);
            if(left.getDistanceValue() > i + 1)
                assignDistanceValues(i + 1, left);
        } 
        if(x < 560) {
            Tile right = rightOf(to);
            if(right.getDistanceValue() > i + 1)
                assignDistanceValues(i + 1, right);
        }
        if(y > 0) {
            Tile up = above(to);
            if(up.getDistanceValue() > i + 1)
                assignDistanceValues(i + 1, up);
        }
        if(y < 560) {
            Tile down = below(to);
            if(down.getDistanceValue() > i + 1)
                assignDistanceValues(i + 1, down);
        } 
    }

    public Tile leftOf(Tile t) {
        return tiles.get(tiles.indexOf(t) - 1);
    }

    public Tile rightOf(Tile t) {
        return tiles.get(tiles.indexOf(t) + 1);
    }

    public Tile above(Tile t) {
        return tiles.get(tiles.indexOf(t) - 15);
    }

    public Tile below(Tile t) {
        return tiles.get(tiles.indexOf(t) + 15);
    }
}