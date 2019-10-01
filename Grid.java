public class Grid
{
    public Node[][] grid;
    public final int sideLength = 720;
    //how much is it zoomed in measured by node's side length
    public int sideLengthPixel = 36;
    //where's the topleft node in the entire grid
    public int topleftX = 0;
    public int topleftY = 0;
    public Grid()
    {
        grid = new Node[sideLength][sideLength];
        for (int i = 0; i < sideLength; i++)
            for (int j = 0; j < sideLength; j++)
                grid[i][j] = new Node(i,j);
    }
}