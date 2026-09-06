import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Esta clase representa el simulador de una maquina tragamonedas.
 * Una maquina tragamonedas tiene varias ruedas (objetos Wheel) y una
 * secuencia de simbolos de colores (objetos Symbol).
 *
 * Decision de diseno: la secuencia de simbolos es UNA SOLA y pertenece
 * a la maquina, no a cada rueda. Esto sale del enunciado: los simbolos
 * aparecen en el mismo orden en todas las ruedas, y por eso addSymbol
 * y delSymbol no reciben el numero de una rueda, mientras que
 * placeSymbol si lo recibe. Lo que distingue a una rueda de otra no
 * son sus simbolos, sino cual de ellos esta mostrando.
 *
 * @author Jhazael y santiago
 * @version 2.0 (Ciclo 1 - 2026-2)
 */
public class SlotMachine {

    // Secuencia de simbolos de la maquina. Todas las ruedas la comparten.
    private ArrayList<Symbol> simbolos;

    // Lista con todas las ruedas que tiene la maquina en este momento.
    private ArrayList<Wheel> ruedas;

    // Indica si el simulador esta en modo visible o invisible.
    private boolean estaVisible;

    // Indica si la ultima operacion que se hizo se pudo realizar bien.
    private boolean operacionExitosa;

    // Se usa para girar las ruedas una cantidad de pasos al azar.
    private Random azar;

    // Estas constantes son solo para calcular donde dibujar cada rueda
    // en la pantalla (no tienen relacion con la logica del negocio).
    private static final int ESPACIO_ENTRE_RUEDAS = 60;
    private static final int POSICION_X_INICIAL = 40;
    private static final int POSICION_Y_INICIAL = 40;

    // Colores que el Canvas de shapes sabe pintar. Con cualquier otro
    // nombre CSS el canvas dibuja negro sin avisar.
    private static final String[] COLORES_VALIDOS = {"red", "black", "blue", 
        "yellow", "green", "magenta", "white"};
    
    // Cuerpo de la maquina. Se dibuja detras de las ruedas y cambia de
    // color cuando la maquina llega a una configuracion ganadora.
    private Rectangle cuerpo;
    
    private static final int MARGEN = 15;

    /**
     * Constructor. Crea una maquina sin ruedas, sin simbolos y en modo
     * invisible, como lo pide el enunciado.
     */
    public SlotMachine() {
        simbolos = new ArrayList<Symbol>();
        ruedas = new ArrayList<Wheel>();
        estaVisible = false;
        
        cuerpo = new Rectangle();
        cuerpo.moveHorizontal(POSICION_X_INICIAL - MARGEN - 70);
        cuerpo.moveVertical(POSICION_Y_INICIAL - MARGEN - 15);
        cuerpo.changeColor("black");
        
        operacionExitosa = true;
        azar = new Random();
    }

    /**
     * Agrega una rueda nueva en la posicion indicada. La rueda nace
     * mostrando el primer simbolo de la secuencia de la maquina.
     * Si la posicion queda fuera de rango se ajusta al extremo mas
     * cercano permitido.
     *
     * @param pos posicion donde se quiere agregar la rueda (empieza en 1).
     */
    public void addWheel(int pos) {
        int indice = ajustarPosicion(pos, ruedas.size() + 1);

        int x = POSICION_X_INICIAL + indice * ESPACIO_ENTRE_RUEDAS;
        Wheel ruedaNueva = new Wheel(simbolos, x, POSICION_Y_INICIAL);
        ruedas.add(indice, ruedaNueva);

        if (estaVisible) {
            ruedaNueva.makeVisible();
        }
        reubicarRuedas();
        actualizarEstadoGanador();
        operacionExitosa = true;
    }
    
    /**
     * Elimina la rueda que esta en la posicion indicada.
     * @param pos posicion de la rueda a eliminar (empieza en 1).
     */
    public void delWheel(int pos) {
        if (ruedas.isEmpty()) {
            fallar("No hay ruedas para eliminar.");
            return;
        }
        int indice = ajustarPosicion(pos, ruedas.size());

        ruedas.get(indice).makeInvisible();
        ruedas.remove(indice);
        
        reubicarRuedas();
        actualizarEstadoGanador();
        operacionExitosa = true;
    }
    
    /**
     * Bloquea la rueda.
     * @param wheel posicion de la rueda a bloquear (empieza en 1).
     */
    public void lock(int wheel){
        if(ruedas.isEmpty()){
            fallar("No se puede ejecutar la accion de bloquear una rueda cuando no hay ruedas");
            return;
        }
        int indice = ajustarPosicion(wheel, ruedas.size()); 
        ruedas.get(indice).bloquearRueda();
        operacionExitosa = true;
        
    }
    
    /**
     * Desbloquea la rueda.
     * @param wheel posicion de la rueda a desbloquear (empieza en 1).
     */
    public void unlock(int wheel){
        if(ruedas.isEmpty()){
            fallar("No se puede ejecutar la accion de desbloquear una rueda cuando no hay ruedas");
            return;
        }
        int indice = ajustarPosicion(wheel, ruedas.size()); 
        ruedas.get(indice).desbloquearRueda();
        operacionExitosa = true;
    }
    
    /**
     * Gira una rueda especifica la cantidad de pasos indicada.
     * Si la rueda esta bloqueada la operacion falla.
     *
     * @param wheel posicion de la rueda a girar (empieza en 1).
     * @param steps cuantos pasos gira la rueda; los negativos giran al reves.
     */
    public void spin(int wheel, int steps) {
        if (ruedas.isEmpty()) {
            fallar("No hay ruedas para girar.");
            return;
        }
        if (simbolos.isEmpty()) {
            fallar("La maquina no tiene simbolos configurados.");
            return;
        }
        int indice = ajustarPosicion(wheel, ruedas.size());

        if (ruedas.get(indice).informarEstadoRueda()) {
            fallar("La rueda esta bloqueada y no se puede girar.");
            return;
        }

        int direccion = steps >= 0 ? 1 : -1;
        int cantidadPasos = Math.abs(steps);
        for (int i = 0; i < cantidadPasos; i++) {
            ruedas.get(indice).rotate(direccion);
        if (estaVisible) {
            Canvas.getCanvas().wait(150);
            }
        }

        actualizarEstadoGanador();
        operacionExitosa = true;
    }
    
    /**
     * Deja la maquina en la configuracion indicada: cada rueda muestra
     * el color que le corresponde en el arreglo.
     * La operacion es atomica: si algo no cuadra no se cambia nada.
     *
     * @param setSymbols colores que debe mostrar cada rueda, de izquierda a derecha.
     */
    public void spin(String[] setSymbols) {
        if (setSymbols == null) {
            fallar("No se recibio una configuracion.");
            return;
        }
        if (setSymbols.length != ruedas.size()) {
            fallar("La configuracion no tiene un color por cada rueda.");
            return;
        }
        for (int i = 0; i < setSymbols.length; i++) {
            if (buscarSimbolo(setSymbols[i]) < 0) {
                fallar("La maquina no tiene un simbolo de color " + setSymbols[i] + ".");
                return;
            }
            if (ruedas.get(i).informarEstadoRueda()
                    && !setSymbols[i].equals(ruedas.get(i).showingColor())) {
                fallar("Una rueda bloqueada tendria que cambiar de simbolo.");
                return;
            }
        }

        for (int i = 0; i < ruedas.size(); i++) {
            ruedas.get(i).place(setSymbols[i]);
        }

        actualizarEstadoGanador();
        operacionExitosa = true;
    }
    
    /**
     * Swap basicamente primero compara si hay dos o mas ruedas si hay menos de una rueda falla si las posiciones son iguales o se intenta
     * intercambiar la misma rueda es no hacer nada falla, no pasan numeros negativos y intercambia las posiciones usando una variable 
     * temporal.
     * 
     * @param wheel1 rueda ingresada primero por el usuario la posicion empieza en 1, si se sale del rango ajusta al extremo mas cercano.
     * @param wheel2 rueda ingresada de segundo por el usuario si se sale del rango ajusta al extremo mas cercano.
     * 
     */
    
    public void swap(int wheel1, int wheel2){
        if(ruedas.size() <= 1){
            fallar("Se necesitan como minimo dos ruedas para hacer un intercambio obvio");
            return;
        }
        int posicion1 = ajustarPosicion(wheel1,ruedas.size());
        int posicion2 = ajustarPosicion(wheel2,ruedas.size());
        if (posicion1 == posicion2) {
            fallar("No se pueden intercambiar dos ruedas que están en la misma posición.");
            return;
        }
        Wheel tempvariabl = ruedas.get(posicion1);
        
        ruedas.set(posicion1,ruedas.get(posicion2));
        ruedas.set(posicion2,tempvariabl);
        
        reubicarRuedas();
        actualizarEstadoGanador();
        operacionExitosa = true;
    }
    
    /**
     * Agrega un simbolo del color indicado a la secuencia de la
     * maquina, en la posicion pedida. El simbolo queda disponible
     * para todas las ruedas.
     *
     * @param pos   posicion donde se quiere el simbolo (empieza en 1).
     * @param color color CSS del simbolo, por ejemplo "red" o "blue".
     */
    public void addSymbol(int pos, String color) {
        if (!colorSoportado(color)) {
            fallar("El color " + color + " no se puede dibujar.");
            return;
        }
        if (buscarSimbolo(color) >= 0) {
            fallar("Ya existe un simbolo de ese color.");
            return;
        }

        int indice = ajustarPosicion(pos, simbolos.size() + 1);
        simbolos.add(indice, new Symbol(color));
        avisarALasRuedas();
        
        actualizarEstadoGanador();
        operacionExitosa = true;
    }

    /**
     * Elimina de la secuencia de la maquina el simbolo que tenga el
     * color indicado.
     *
     * @param symbol color del simbolo a eliminar.
     */
    public void delSymbol(String symbol) {
        int indice = buscarSimbolo(symbol);
        if (indice < 0) {
            fallar("No existe un simbolo de ese color.");
            return;
        }

        simbolos.remove(indice);
        avisarALasRuedas();
        
        actualizarEstadoGanador();
        operacionExitosa = true;
    }

    /**
     * Deja fijo, en la rueda indicada, el simbolo del color indicado
     * como el simbolo que se esta mostrando.
     * Si la rueda esta bloqueada la operacion falla.
     *
     * @param wheel  posicion de la rueda (empieza en 1).
     * @param symbol color del simbolo que se quiere mostrar.
     */
    public void placeSymbol(int wheel, String symbol) {
        if (ruedas.isEmpty()) {
            fallar("No hay ruedas configuradas.");
            return;
        }
        int indice = ajustarPosicion(wheel, ruedas.size());

        if (ruedas.get(indice).informarEstadoRueda()) {
            fallar("La rueda esta bloqueada y no se puede cambiar su simbolo.");
            return;
        }

        if (!ruedas.get(indice).place(symbol)) {
            fallar("La maquina no tiene un simbolo de ese color.");
            return;
        }

        actualizarEstadoGanador();
        operacionExitosa = true;
    }

    /**
     * Gira una sola rueda de la maquina una cantidad de pasos al azar.
     * Si la rueda esta bloqueada la operacion falla.
     *
     * @param wheel posicion de la rueda a girar (empieza en 1).
     */
    public void spin(int wheel) {
        if (ruedas.isEmpty()) {
            fallar("No hay ruedas para girar.");
            return;
        }
        if (simbolos.isEmpty()) {
            fallar("La maquina no tiene simbolos configurados.");
            return;
        }
        int indice = ajustarPosicion(wheel, ruedas.size());

        if (ruedas.get(indice).informarEstadoRueda()) {
            fallar("La rueda esta bloqueada y no se puede girar.");
            return;
        }

        ruedas.get(indice).rotate(azar.nextInt(simbolos.size()));

        actualizarEstadoGanador();
        operacionExitosa = true;
    }

    /**
     * Gira todas las ruedas de la maquina, una por una.
     * Las ruedas bloqueadas se saltan y la operacion sigue siendo exitosa.
     */
    public void spin() {
        if (ruedas.isEmpty()) {
            fallar("No hay ruedas para girar.");
            return;
        }
        if (simbolos.isEmpty()) {
            fallar("La maquina no tiene simbolos configurados.");
            return;
        }

        for (int i = 0; i < ruedas.size(); i++) {
            if (ruedas.get(i).informarEstadoRueda()) {
                continue;
            }
            ruedas.get(i).rotate(azar.nextInt(simbolos.size()));
        }

        actualizarEstadoGanador();
        operacionExitosa = true;
    }

    /**
     * @return los colores de los simbolos de la maquina, en el mismo
     * orden en que estan en la secuencia (empezando en la posicion 1).
     */
    public String[] symbols() {
        String[] colores = new String[simbolos.size()];
        for (int i = 0; i < simbolos.size(); i++) {
            colores[i] = simbolos.get(i).getColor();
        }
        operacionExitosa = true;
        return colores;
    }

    /**
     * @return la cantidad de colores distintos que se estan viendo en
     * las ruedas en este momento. Es la k del problema original: si
     * vale 1, todas las ruedas muestran lo mismo.
     */
    public int distinctSymbols() {
        String[] visibles = configuration();
        ArrayList<String> vistos = new ArrayList<String>();
        for (int i = 0; i < visibles.length; i++) {
            if (visibles[i] != null && !vistos.contains(visibles[i])) {
                vistos.add(visibles[i]);
            }
        }
        operacionExitosa = true;
        return vistos.size();
    }

    /**
     * @return el color que esta mostrando cada rueda en este momento,
     * ordenados de izquierda a derecha.
     */
    public String[] configuration() {
        String[] coloresVisibles = new String[ruedas.size()];
        for (int i = 0; i < ruedas.size(); i++) {
            coloresVisibles[i] = ruedas.get(i).showingColor();
        }
        operacionExitosa = true;
        return coloresVisibles;
    }

    /**
     * @return true si la maquina esta en configuracion ganadora, es
     * decir, si todas las ruedas muestran el mismo simbolo.
     */
    public boolean isJackpot() {
        return !ruedas.isEmpty() && distinctSymbols() == 1;
    }

    /**
     * Hace visible todo el simulador.
     */
    public void makeVisible() {
        estaVisible = true;
        cuerpo.makeVisible();
        for (int i = 0; i < ruedas.size(); i++) {
            ruedas.get(i).makeVisible();
        }
        operacionExitosa = true;
    }

    /**
     * Hace invisible todo el simulador.
     */
    public void makeInvisible() {
        estaVisible = false;
        for (int i = 0; i < ruedas.size(); i++) {
            ruedas.get(i).makeInvisible();
        }
        cuerpo.makeInvisible();
        operacionExitosa = true;
    }

    /**
     * Termina el simulador. Por ahora simplemente oculta todo.
     */
    public void exit() {
        makeInvisible();
        operacionExitosa = true;
    }

    /**
     * @return true si la ultima operacion que se invoco se pudo
     * realizar correctamente, false si no.
     */
    public boolean ok() {
        return operacionExitosa;
    }

    /**
     * Ajusta una posicion dada por el usuario para que quede dentro
     * del rango permitido, y la convierte al indice de la lista.
     * El enunciado pide que si el valor se sale, se use el extremo
     * mas cercano en vez de rechazar la operacion.
     *
     * @param pos    posicion pedida por el usuario (empieza en 1).
     * @param maximo posicion mas grande que se acepta.
     * @return el indice equivalente en la lista (empieza en 0).
     */
    private int ajustarPosicion(int pos, int maximo) {
        int posicionValida = pos;
        if (posicionValida < 1) {
            posicionValida = 1;
        }
        if (posicionValida > maximo) {
            posicionValida = maximo;
        }
        return posicionValida - 1;
    }

    /**
     * Busca en la secuencia el simbolo que tenga el color indicado.
     *
     * @param color color que se esta buscando.
     * @return la posicion del simbolo en la lista, o -1 si no existe.
     */
    private int buscarSimbolo(String color) {
        for (int i = 0; i < simbolos.size(); i++) {
            if (simbolos.get(i).getColor().equals(color)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * @param color nombre de color que se quiere verificar.
     * @return true si el canvas sabe dibujar ese color.
     */
    private boolean colorSoportado(String color) {
        for (int i = 0; i < COLORES_VALIDOS.length; i++) {
            if (COLORES_VALIDOS[i].equals(color)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Le avisa a todas las ruedas que la secuencia de simbolos cambio,
     * para que ajusten lo que estan mostrando.
     */
    private void avisarALasRuedas() {
        for (int i = 0; i < ruedas.size(); i++) {
            ruedas.get(i).refresh();
        }
    }
    
        /**
     * Vuelve a dibujar las ruedas para que queden por encima del cuerpo.
     * El canvas de shapes pinta las figuras en el orden en que se
     * dibujaron por ultima vez, asi que cada vez que el cuerpo cambia
     * de color o de tamano queda al frente y hay que subir las ruedas.
     */
    private void traerRuedasAlFrente() {
        if (!estaVisible) {
            return;
        }
        for (int i = 0; i < ruedas.size(); i++) {
            ruedas.get(i).makeVisible();
        }
    }
    
    /**
     * Vuelve a poner cada rueda en su lugar de izquierda a derecha y
     * ajusta el tamano del cuerpo. Se llama cada vez que cambia el
     * numero de ruedas, para que no queden huecos.
     */
    private void reubicarRuedas() {
        for (int i = 0; i < ruedas.size(); i++) {
            ruedas.get(i).moveTo(POSICION_X_INICIAL + i * ESPACIO_ENTRE_RUEDAS,
                                 POSICION_Y_INICIAL);
        }
        cuerpo.changeSize(30 + 2 * MARGEN, ruedas.size() * ESPACIO_ENTRE_RUEDAS + 10);
        traerRuedasAlFrente();
    }
    
    
    /**
     * Requisito de usabilidad: la maquina debe lucir diferente cuando
     * llega al estado ganador. El cuerpo se pone amarillo.
     */
    private void actualizarEstadoGanador() {
        if (isJackpot()) {
            cuerpo.changeColor("yellow");
        } else {
            cuerpo.changeColor("black");
        }
        traerRuedasAlFrente();
    }
    
    /**
     * Marca la ultima operacion como fallida y le avisa al usuario,
     * pero SOLO si el simulador esta visible. Si esta invisible, la
     * operacion falla en silencio (requisito de usabilidad numero 4).
     *
     * @param mensaje texto que se le quiere mostrar al usuario.
     */
    private void fallar(String mensaje) {
        operacionExitosa = false;
        if (estaVisible) {
            JOptionPane.showMessageDialog(null, mensaje,
                    "Slot Machine", JOptionPane.WARNING_MESSAGE);
        }
    }
}