import java.util.*;
import java.io.*;

class Jugador{
    private String rut; //adicion del atributo RUT
    private String nombreApellido; //cambio de nombre a playedName
    private int wins;
    private int losses;
    private int draws;

    public Jugador(String rut, String nombreApellido){
        this.rut=rut;
        this.nombreApellido=nombreApellido;
        wins=0;
        losses=0;
        draws=0;
    }
    public void addWin(){
        wins++;
    }
    public void addLoss(){
        losses++;
    }
    public void addDraw(){
        draws++;
    }
    public float winRate(){
        int partidas= wins+draws+losses;
        if(partidas==0){
            return 0;
        }
        float winrate= (float) wins/partidas;
        winrate= Math.round(winrate*1000)/1000.0f;
        return winrate;
    }

    //Metodos añadidos
    public String getRut(){
        return rut;
    } //Retorna rut de un jugador
    public String getNombreApellido(){
        return nombreApellido;
    } //Retorna el nombre y apellido
    public int getWins(){
        return wins;
    } //Retorna las victorias
    public int getDraws(){
        return draws;
    } //Retorna los empates
    public int getLosses(){
        return losses;
    } //Retorna las derrotas
}

class Scoreboard{
    private TreeMap<Integer, List<String>> winTree;
    private HashMap<String, Jugador> players;
    private int playedGames;

    public Scoreboard(){
        winTree = new TreeMap<>();
        players = new HashMap<>();
        playedGames=0;
    }
    public void addGameResult(String winnerRut, String looserRut, boolean draw){
        registerPlayer(winnerRut,"(SinNombre)");
        registerPlayer(looserRut,"(SinNombre)");
        Jugador winner= players.get(winnerRut);
        Jugador looser= players.get(looserRut);
        removePlayer_TreeMap(winner);
        removePlayer_TreeMap(looser);
        if(draw){
            winner.addDraw();
            looser.addDraw();
        }
        else{
            winner.addWin();
            looser.addLoss();
        }
        addPlayer_TreeMap(winner);
        addPlayer_TreeMap(looser);
        playedGames++;
    }
    public void registerPlayer(String rut, String nombreApellido){
        if(!players.containsKey(rut)){
            Jugador nuevo = new Jugador(rut, nombreApellido);
            players.put(rut,nuevo);
            addPlayer_TreeMap(nuevo);
        }
    }
    public boolean checkPlayer(String rut){
        return players.containsKey(rut);
    }

    //Metodos adicionales
    public void addPlayer(String rut, String nombreApellido){
        if(!players.containsKey(rut)){
            Jugador nuevo = new Jugador(rut, nombreApellido);
            players.put(rut,nuevo);
            addPlayer_TreeMap(nuevo);
        }
    } //añadir player
    public void removePlayer(String rut){
        if(players.containsKey(rut)){
            Jugador j= players.get(rut);
            removePlayer_TreeMap(j);
            players.remove(rut);
        }
    } //remover player
    public Jugador getPlayer(String rut){
        return players.get(rut);
    } //retornar player
    public static boolean isRutValido(String rut){
        return rut!=null && rut.matches("\\d{7,8}[kK\\d]");
    } //validar si existe el rut ingresado
    public List<Jugador> getAllPlayers(){
        return new ArrayList<>(players.values());
    } //retornar lista de players
    public static String pedirRut(Scanner sc, String mensaje){
        while(true){
            System.out.print(mensaje);
            String rut=sc.nextLine().trim();
            if(isRutValido(rut)){
                return rut;
            }
            System.out.println("Rut inválido. Ingresa solo números, sin puntos ni guión, puede terminar en K.");
        }
    } //metodo para ingresar rut
    public List<Jugador> playerPorRanking(){
        List<Jugador> lista= getAllPlayers();
        lista.sort((a,b)->{
            if(b.getWins()!=a.getWins()){
                return Integer.compare(b.getWins(),a.getWins());
            }
            else{
                return Float.compare(b.winRate(),a.winRate());
            }
        });
        return lista;
    } //obtener players segun ranking (del mejor al peor segun victorias)
    public List<Jugador> buscarPorWins(int wins){
        List<Jugador> result = new ArrayList<>();
        for(Jugador j : players.values()){
            if(j.getWins()==wins)result.add(j);
        }
        return result;
    } //reemplazo de winRange(int lo, int hi), metodo solicitado para buscar por cantidad de victorias o cercania.
    public List<Jugador> PlayerMenosWins(int wins){
        int menor=-1;
        for(Jugador j : players.values()){
            int w=j.getWins();
            if(w<wins && w>menor){
                menor=w;
            }
        }
        List<Jugador> result = new ArrayList<>();
        for(Jugador j : players.values()){
            if(j.getWins()==menor){
                result.add(j);
            }
        }
        return result;
    } //retornar players con menos victorias
    public List<Jugador> PlayerMasWins(int wins){
        int mayor= Integer.MAX_VALUE;
        for(Jugador j : players.values()){
            int w= j.getWins();
            if(w>wins && w<mayor){
                mayor=w;
            }
        }
        List<Jugador> result=new ArrayList<>();
        for(Jugador j:players.values()){
            if(j.getWins()==mayor){
                result.add(j);
            }
        }
        return result;
    } //retorna lista de players con más victorias
    private void removePlayer_TreeMap(Jugador jugador){
        int wins= jugador.getWins();
        if(winTree.containsKey(wins)){
            List<String> lista = winTree.get(wins);
            while(lista.remove(jugador.getRut())){}
            if(lista.isEmpty()){
                winTree.remove(wins);
            }
        }
    } //remover player del arbol (scoreboard)
    private void addPlayer_TreeMap(Jugador jugador){
        int wins= jugador.getWins();
        winTree.putIfAbsent(wins, new ArrayList<>());
        List<String> lista = winTree.get(wins);
        if(!lista.contains(jugador.getRut())){
            lista.add(jugador.getRut());
        }
    } //añade un player al arbol (scoreboard)
}

class ConnectFour{
    private char[][] grid;
    private char currentSymbol;

    public ConnectFour(){
        grid = new char[7][6];
        for(int col=0; col<7; col++){
            for(int fila=0; fila<6; fila++){
                grid[col][fila]=' ';
            }
        }
        currentSymbol='X';
    }
    public boolean makeMove(int columna){
        if(columna<0 || columna>=7){
            return false;
        }
        for(int fila=0; fila<6; fila++){
            if(grid[columna][fila]==' '){
                grid[columna][fila]=currentSymbol;
                currentSymbol=(currentSymbol=='X') ? 'O' : 'X';
                return true;
            }
        }
        return false;
    }
    public String isGameOver(){
        for(int col=0; col<7; col++){
            for(int fila=0; fila<6; fila++){
                char c=grid[col][fila];
                if(c==' ')continue;
                if((col+3)<7 && (c==grid[col+1][fila]) && (c==grid[col+2][fila]) && (c==grid[col+3][fila])){
                    return String.valueOf(c);
                }
                if((fila+3)<6 && (c==grid[col][fila+1]) && (c==grid[col][fila+2]) && (c==grid[col][fila+3])){
                    return String.valueOf(c);
                }
                if((col+3)<7 && (fila+3)<6 && (c==grid[col+1][fila+1]) && (c==grid[col+2][fila+2]) && (c==grid[col+3][fila+3])){
                    return String.valueOf(c);
                }
                if((col+3)<7 && (fila-3)>=0 && (c==grid[col+1][fila-1]) && (c==grid[col+2][fila-2]) && (c==grid[col+3][fila-3])){
                    return String.valueOf(c);
                }
            }
        }
        for(int col=0; col<7; col++){
            if(grid[col][5]==' '){
                return null;
            }
        }
        return "DRAW";
    }

    //Metodos añadidos
    public void printBoard(){
        System.out.println(" 0 1 2 3 4 5 6 ");
        for(int fila=5; fila>=0; fila--){
            for(int col=0; col<7; col++){
                System.out.print("|" + grid[col][fila]);
            }
            System.out.println("|");
        }
        System.out.println("----------------");
    } //Imprime el tablero y el numero de casillas, actualizando cada vez que se juega un turno
}

class Game{
    private String status;
    private Jugador jugadorA;
    private Jugador jugadorB;
    private ConnectFour tablero;
    private Scoreboard ranking; //Cambio winnerPlayerName por rankign, para manipular y mostrar la tabla de jugadores

    public Game(Jugador jugadorA, Jugador jugadorB, Scoreboard ranking){
        this.jugadorA=jugadorA;
        this.jugadorB=jugadorB;
        this.tablero = new ConnectFour();
        this.ranking=ranking;
        this.status="IN_PROGRESS";
    }
    public void play(Scanner sc){
        String currentRut=jugadorA.getRut();
        String currentName=jugadorA.getNombreApellido();
        boolean enCurso=true;
        if(status.equals("IN_PROGRESS")){
            System.out.println("COMIENZA EL JUEGO");
        }
        while(enCurso){
            tablero.printBoard();
            System.out.println("Turno de " + currentName + " [" + (currentRut.equals(jugadorA.getRut()) ? "X" : "O") + "]");
            int col=pedirColumna(sc);
            if(!tablero.makeMove(col)){
                System.out.println("Movimiento inválido, intenta otra columna.");
                continue;
            }
            String res=tablero.isGameOver();
            if(res!=null){
                tablero.printBoard();
                if(res.equals("DRAW")){
                    actualizarStatus("DRAW");
                    System.out.println("- EMPATE -");
                    jugadorA.addDraw();
                    jugadorB.addDraw();
                }
                else{
                    actualizarStatus("VICTORY");
                    Jugador ganador=res.equals("X") ? jugadorA : jugadorB;
                    Jugador perdedor=res.equals("X") ? jugadorB : jugadorA;
                    ganador.addWin();
                    perdedor.addLoss();
                    System.out.println("¡VICTORIA!");
                    System.out.println("¡Ganador: " + ganador.getNombreApellido() + "!");
                }
                enCurso=false;
            }
            else{
                if(currentRut.equals(jugadorA.getRut())){
                    currentRut=jugadorB.getRut();
                    currentName=jugadorB.getNombreApellido();
                }
                else{
                    currentRut=jugadorA.getRut();
                    currentName=jugadorA.getNombreApellido();
                }
            }
        }
    }

    //Metodos implementados
    public void actualizarStatus(String nuevoEstado){
        this.status = nuevoEstado;
    } //Actualiza el valor de status
    private int pedirColumna(Scanner sc){
        while(true){
            System.out.print("Columna (0-6): ");
            try{
                int col=Integer.parseInt(sc.nextLine());
                if(col>=0 && col<7){
                    return col;
                }
            }catch(Exception e){}
            System.out.println("Columna inválida, intenta de nuevo.");
        }
    } //Jugabilidad del tablero
    public static void mostrarTablaJugadores(List<Jugador> lista, int max){
        System.out.println("+----+-----------------------------+--------+---------+" +
                "\n| #  | Nombre y Apellido (Player)  |  Wins  | Winrate |" +
                "\n+----+-----------------------------+--------+---------+");
        int pos=1;
        for(Jugador j : lista){
            if(pos>max){
                break;
            }
            double porcentaje= Math.round(j.winRate()*1000.0)/10.0;
            System.out.printf("| %2d | %-27s | %6d | %6.2f%% |\n", pos, j.getNombreApellido(), j.getWins(), porcentaje);
            pos++;
        }
        System.out.println("+----+-----------------------------+--------+---------+");
    } //Atractivo visual
}

public class Main{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        Scoreboard Ranking = new Scoreboard();
        boolean continuar=true;
        while(continuar){
            System.out.print("\n===== MENÚ PRINCIPAL =====" +
                    "\n1) Empezar juego" +
                    "\n2) Ver Ranking" +
                    "\n3) Añadir / Borrar Jugador" +
                    "\n4) Buscar por número de victorias" +
                    "\n0) Cerrar Programa" +
                    "\nSelecciona una opción: ");
            String opcion=sc.nextLine();
            switch(opcion){
                case "1":
                    System.out.println("\n--[Empezar juego]--");
                    String rutA= Scoreboard.pedirRut(sc, "Rut participante 1 (ficha roja/X), sin puntos ni guion: ");
                    Jugador jugadorA;
                    if(Ranking.checkPlayer(rutA)){
                        jugadorA=Ranking.getPlayer(rutA);
                    }
                    else{
                        System.out.print("Jugador no existe. Ingresa nombre y apellido: ");
                        String nombreA=sc.nextLine();
                        Ranking.addPlayer(rutA, nombreA);
                        jugadorA=Ranking.getPlayer(rutA);
                    }
                    String rutB= Scoreboard.pedirRut(sc, "Rut participante 2 (ficha amarilla/O), sin puntos ni guion: ");
                    Jugador jugadorB;
                    if(Ranking.checkPlayer(rutB)){
                        jugadorB=Ranking.getPlayer(rutB);
                    }
                    else{
                        System.out.print("Jugador no existe. Ingresa nombre y apellido: ");
                        String nombreB=sc.nextLine();
                        Ranking.addPlayer(rutB, nombreB);
                        jugadorB=Ranking.getPlayer(rutB);
                    }
                    Game partida = new Game(jugadorA, jugadorB, Ranking);
                    partida.play(sc);
                    break;
                case "2":
                    System.out.println("\n--[Ver Ranking]--");
                    List<Jugador> ranking= Ranking.playerPorRanking();
                    if(ranking.size()<=10){
                        Game.mostrarTablaJugadores(ranking, ranking.size());
                    }
                    else{
                        Game.mostrarTablaJugadores(ranking, 10);
                        System.out.println("\n1) Ver resto de jugadores" +
                                "\n2) Volver");
                        String op=sc.nextLine();
                        if(op.equals("1")){
                            Game.mostrarTablaJugadores(ranking, ranking.size());
                        }
                    }
                    break;
                case "3":
                    System.out.println("\n--[Añadir / Borrar Jugador]--");
                    List<Jugador> todos= Ranking.playerPorRanking();
                    Game.mostrarTablaJugadores(todos, todos.size());
                    System.out.println("\n1) Añadir Jugador" +
                            "\n2) Borrar Jugador" +
                            "\n0) Cancelar");
                    String op2=sc.nextLine();
                    switch(op2){
                        case "1":
                            String rut= Scoreboard.pedirRut(sc, "Ingresa el rut del jugador, sin puntos ni guion: ");
                            System.out.print("Ingresa el nombre y apellido del jugador: ");
                            String nombre=sc.nextLine();
                            if(!Ranking.checkPlayer(rut)){
                                Ranking.addPlayer(rut, nombre);
                                System.out.println("Jugador añadido correctamente.");
                            }
                            else{
                                System.out.println("Ya existe un jugador con ese rut.");
                            }
                            break;
                        case "2":
                            String rutBorrar= Scoreboard.pedirRut(sc, "Ingresa el rut del jugador a borrar, sin puntos ni guion: ");
                            if(Ranking.checkPlayer(rutBorrar)){
                                Jugador j= Ranking.getPlayer(rutBorrar);
                                System.out.print("¿Seguro de eliminar a " + j.getNombreApellido() + "? (s/n)" +
                                        "\nRespuesta: ");
                                String confirm=sc.nextLine();
                                if(confirm.equalsIgnoreCase("s")){
                                    Ranking.removePlayer(rutBorrar);
                                    System.out.println("Jugador eliminado.");
                                }
                                else{
                                    System.out.println("Operación cancelada.");
                                }
                            }
                            else{
                                System.out.println("No existe jugador con ese rut.");
                            }
                            break;
                        default:
                            System.out.println("Volviendo al menú principal.");
                            break;
                    }
                    break;
                case "4":
                    System.out.println("\n--[Buscar por número de victorias]--" +
                            "\nIngresa número de victorias a buscar: ");
                    int victorias=Integer.parseInt(sc.nextLine());
                    List<Jugador> encontrados= Ranking.buscarPorWins(victorias);
                    if(encontrados.isEmpty()){
                        List<Jugador> menor= Ranking.PlayerMenosWins(victorias);
                        List<Jugador> mayor= Ranking.PlayerMasWins(victorias);
                        if(!menor.isEmpty()){
                            System.out.println("No hay jugadores con " + victorias + " victorias. Jugadores con el número menor más cercano:");
                            for(Jugador j : menor){
                                double porcentaje= Math.round(j.winRate()*1000.0)/10.0;
                                System.out.printf("| %-26s | Wins: %2d | Winrate: %6.2f%% |\n", j.getNombreApellido(), j.getWins(), porcentaje);
                            }
                        }
                        else if(!mayor.isEmpty()){
                            System.out.println("No hay jugadores con " + victorias + " victorias. Jugadores con el número mayor más cercano:");
                            for(Jugador j : mayor){
                                double porcentaje= Math.round(j.winRate()*1000.0)/10.0;
                                System.out.printf("| %-26s | Wins: %2d | Winrate: %6.2f%% |\n", j.getNombreApellido(), j.getWins(), porcentaje);
                            }
                        }
                        else{
                            System.out.println("No hay jugadores registrados.");
                        }
                    }
                    else{
                        for(Jugador j : encontrados){
                            double porcentaje= Math.round(j.winRate()*1000.0)/10.0;
                            System.out.printf("| %-26s | Wins: %2d | Winrate: %6.2f%% |\n", j.getNombreApellido(), j.getWins(), porcentaje);
                        }
                    }
                    break;
                default:
                    System.out.println("¡Programa finalizado!, Adiós broder.");
                    continuar=false;
                    break;
            }
        }
    }
}