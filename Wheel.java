import java.util.ArrayList;
 
/**
 * Esta clase representa una rueda de la maquina tragamonedas.
 *
 * Como lo diseñamos: la rueda NO es dueña de los simbolos ya que todas las
 * ruedas comparten la misma secuencia, que vive en SlotMachine, porque
 * el enunciado dice que los simbolos aparecen en el mismo orden en
 * todas las ruedas y la rueda solo recuerda cual de esos simbolos esta
 * mostrando por su ventana.
 *
 * Aunque la secuencia sea compartida, cada rueda
 * dibuja con su propio objeto Symbol. Dos ruedas pueden mostrar el
 * mismo simbolo al mismo tiempo y un mismo rectangulo no puede estar 
 * en dos lugares de la pantalla.
 *
 * @author Jhazael y Santiago
 * @version 1.0 (Ciclo 1 - 2026-2)
 */
public class Wheel {
 
    // Secuencia de simbolos compartida con la maquina. La rueda solo
    // la lee: nunca agrega ni elimina simbolos de ella.
    private ArrayList<Symbol> secuencia;
 
    // Posicion, dentro de la secuencia, del simbolo que se muestra.
    private int indiceMostrado;
 
    // El rectangulo propio de esta rueda, el que se ve en pantalla.
    private Symbol simboloVisible;
 
    // Donde se dibuja esta rueda en el canvas.
    private int posicionX;
    private int posicionY;
 
    // Indica si esta rueda esta en modo visible.
    private boolean estaVisible;
    
    // Indicia si la rueda esta bloqueada.
    private boolean bloqueada;
 
    /**
     * Crea una rueda que lee la secuencia de simbolos indicada y que
     * se dibuja en la posicion (x, y) del canvas.
     *
     * @param secuencia lista de simbolos compartida con la maquina.
     * @param x posicion horizontal de la rueda en pixeles.
     * @param y posicion vertical de la rueda en pixeles.
     */
    public Wheel(ArrayList<Symbol> secuencia, int x, int y) {
        this.secuencia = secuencia;
        this.indiceMostrado = 0;
        this.bloqueada = false;
        this.posicionX = x;
        this.posicionY = y;
        this.estaVisible = false;
        this.simboloVisible = new Symbol("black");
        this.simboloVisible.placeAt(x, y);
        actualizarSimboloVisible();
    }
    
    /**
     * actualiza el estado de la rueda a false.
     */
    public void desbloquearRueda(){
         bloqueada = false;
    }
    
    /**
     * actualiza el estado de la rueda a true.
     */
    public void bloquearRueda(){
         bloqueada = true;
    }
    
    /**
     * retorna el valor de la rueda que esté en ese momento.
     * @return true si está bloqueada, false si no.
     */
    public boolean informarEstadoRueda(){
         return bloqueada;
    }
    
    /**
     * Gira la rueda la cantidad de pasos indicada. Al pasarse del
     * final de la secuencia vuelve al principio, porque la rueda es
     * circular. Los pasos negativos giran en sentido contrario.
     *
     * @param pasos cuantas posiciones avanza el simbolo mostrado.
     */
    public void rotate(int pasos) {
        if (secuencia.isEmpty()) {
            return;
        }
        int cantidad = secuencia.size();
        // el doble modulo es para que un giro negativo tambien caiga
        // dentro del rango 0..cantidad-1
        indiceMostrado = ((indiceMostrado + pasos) % cantidad + cantidad) % cantidad;
        actualizarSimboloVisible();
    }
 
    /**
     * Deja fijo, como simbolo mostrado, el que tenga el color indicado.
     *
     * @param color color del simbolo que se quiere mostrar.
     * @return true si existe un simbolo de ese color; false si no.
     */
    public boolean place(String color) {
        for (int i = 0; i < secuencia.size(); i++) {
            if (secuencia.get(i).getColor().equals(color)) {
                indiceMostrado = i;
                actualizarSimboloVisible();
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return el color que esta rueda esta mostrando en este momento,
     * o null si todavia no hay simbolos en la secuencia.
     */
    public String showingColor() {
        if (secuencia.isEmpty()) {
            return null;
        }
        return secuencia.get(indiceMostrado).getColor();
    }
 
    /**
     * La maquina llama a este metodo cuando la secuencia de simbolos
     * cambio, para que la rueda ajuste su indice si quedo apuntando
     * fuera de la lista y vuelva a pintar lo que corresponde.
     */
    public void refresh() {
        if (secuencia.isEmpty()) {
            indiceMostrado = 0;
        } else if (indiceMostrado >= secuencia.size()) {
            indiceMostrado = secuencia.size() - 1;
        }
        actualizarSimboloVisible();
    }
 
    /**
     * Mueve la rueda a otra posicion del canvas. Se usa cuando se
     * elimina una rueda y las demas se tienen que reacomodar.
     *
     * @param x nueva posicion horizontal en pixeles.
     * @param y nueva posicion vertical en pixeles.
     */
    public void moveTo(int x, int y) {
        posicionX = x;
        posicionY = y;
        simboloVisible.placeAt(x, y);
    }
 
    /**
     * Hace visible esta rueda.
     */
    public void makeVisible() {
        estaVisible = true;
        actualizarSimboloVisible();
    }
 
    /**
     * Hace invisible esta rueda.
     */
    public void makeInvisible() {
        estaVisible = false;
        simboloVisible.makeInvisible();
    }
 
    /**
     * Pone el rectangulo de esta rueda del color del simbolo que le
     * corresponde mostrar, y lo dibuja solo si la rueda esta visible.
     * Si la secuencia esta vacia no hay nada que mostrar.
     */
    private void actualizarSimboloVisible() {
        if (secuencia.isEmpty()) {
            simboloVisible.makeInvisible();
            return;
        }
        simboloVisible.changeColor(secuencia.get(indiceMostrado).getColor());
        if (estaVisible) {
            simboloVisible.makeVisible();
        } else {
            simboloVisible.makeInvisible();
        }
    }
}