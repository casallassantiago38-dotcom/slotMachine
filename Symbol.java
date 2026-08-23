/**
 * Esta clase representa un simbolo de la maquina tragamonedas.
 * Un simbolo se identifica por su color, y se dibuja en pantalla como
 * un rectangulo. Por eso esta clase extiende (hereda de) Rectangle:
 * un Symbol ES un tipo especial de Rectangle, que ademas recuerda su
 * color y su posicion en pantalla.
 *
 * @author Jhazael y Santiago
 * @version 1.1 (Ciclo 1 - 2026-2)
 */
public class Symbol extends Rectangle {

    // Color CSS que identifica a este simbolo, por ejemplo "red".
    private String color;

    // Rectangle guarda la posicion (x, y) como atributos privados, y
    // nosotros no podemos leerlos directamente desde esta clase.
    // Por eso llevamos nuestra propia copia de la posicion aqui.
    private int posicionActualX;
    private int posicionActualY;

    // Recordamos si este simbolo esta visible en este momento.
    private boolean estaVisible;

    /**
     * Crea un simbolo nuevo del color indicado. Nace invisible,
     * igual que lo hace Rectangle por defecto.
     *
     * @param color nombre de color CSS, por ejemplo "red" o "blue".
     */
    public Symbol(String color) {
        super();
        this.color = color;
        changeColor(color);

        // estos valores deben coincidir con la posicion inicial que
        // usa el constructor de Rectangle (xPosition = 70, yPosition = 15)
        posicionActualX = 70;
        posicionActualY = 15;
        estaVisible = false;
    }

    /**
     * Mueve este simbolo hasta la posicion (x, y) indicada, usando
     * los metodos moveHorizontal y moveVertical que ya trae Rectangle.
     *
     * @param x nueva posicion horizontal en pixeles.
     * @param y nueva posicion vertical en pixeles.
     */
    public void placeAt(int x, int y) {
        int distanciaHorizontal = x - posicionActualX;
        int distanciaVertical = y - posicionActualY;
        moveHorizontal(distanciaHorizontal);
        moveVertical(distanciaVertical);
    }

    /**
     * @return el color CSS que identifica a este simbolo.
     */
    public String getColor() {
        return color;
    }

    /**
     * Hace visible este simbolo en el canvas. Sobreescribimos este
     * metodo solo para poder actualizar nuestra variable estaVisible.
     */
    @Override
    public void makeVisible() {
        super.makeVisible();
        estaVisible = true;
    }

    @Override
    public void changeColor(String nuevoColor) {
        super.changeColor(nuevoColor);
        this.color = nuevoColor;
    }
    
    /**
     * Hace invisible este simbolo. Igual que arriba, sobreescribimos
     * solo para mantener actualizada nuestra variable estaVisible.
     */
    @Override
    public void makeInvisible() {
        super.makeInvisible();
        estaVisible = false;
    }

    /**
     * Mueve el simbolo horizontalmente. Sobreescribimos este metodo
     * para actualizar nuestra copia de la posicion (posicionActualX).
     *
     * @param distancia cuantos pixeles se debe mover (puede ser negativo).
     */
    @Override
    public void moveHorizontal(int distancia) {
        super.moveHorizontal(distancia);
        posicionActualX = posicionActualX + distancia;
    }

    /**
     * Mueve el simbolo verticalmente. Sobreescribimos este metodo
     * para actualizar nuestra copia de la posicion (posicionActualY).
     *
     * @param distancia cuantos pixeles se debe mover (puede ser negativo).
     */
    @Override
    public void moveVertical(int distancia) {
        super.moveVertical(distancia);
        posicionActualY = posicionActualY + distancia;
    }
}
