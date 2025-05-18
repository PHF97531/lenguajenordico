import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

// Clase Token que almacena información de cada palabra analizada
class Token {
    String tipo;
    String valor;

    public Token(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return tipo + ": " + valor;
    }
}

// Clase Lexer que analiza el código y genera tokens
class Lexer {
    private static final Pattern patron = Pattern.compile(
            "\\b(Eg|Tú|Hann)\\b|\\b(eri|ert|er)\\b|\\b(heima|skúla)\\b|[0-9]+|[=+\\-*/]" // Se agregan números y operadores
    );

    public static List<Token> analizar(String codigo) {
        Matcher matcher = patron.matcher(codigo);
        List<Token> tokens = new ArrayList<>();

        while (matcher.find()) {
            String palabra = matcher.group();
            String tipo = switch (palabra) {
                case "Eg", "Tú", "Hann" -> "Sujeto";
                case "eri", "ert", "er" -> "Verbo";
                case "heima", "skúla" -> "Objeto";
                case "=" -> "Asignación";
                case "+", "-", "*", "/" -> "Operador Matemático";
                default -> palabra.matches("[0-9]+") ? "Número" : "Desconocido";
            };
            tokens.add(new Token(tipo, palabra));
        }

        return tokens;
    }
}

// Clase que valida la gramática BNF
class Parser {
    public static boolean validarBNF(List<Token> tokens) {
        return tokens.size() == 3 &&
                tokens.get(0).tipo.equals("Sujeto") &&
                tokens.get(1).tipo.equals("Verbo") &&
                tokens.get(2).tipo.equals("Objeto");
    }
}

// Clase Principal con Interfaz Gráfica
public class Main {
    public static void main(String[] args) {
        // Creación de la ventana principal
        JFrame frame = new JFrame("Analizador Léxico y Sintáctico - Lenguaje Feroés");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Panel superior: Campo de texto y botón
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new FlowLayout());

        JTextArea textArea = new JTextArea(5, 40);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JButton analizarBtn = new JButton("Analizar");

        panelSuperior.add(scrollPane);
        panelSuperior.add(analizarBtn);

        // Área de salida para mostrar los resultados
        JTextArea resultadoArea = new JTextArea(10, 50);
        resultadoArea.setEditable(false);
        JScrollPane resultadoScroll = new JScrollPane(resultadoArea);

        // Acción al presionar el botón
        analizarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codigo = textArea.getText().trim(); // Eliminamos espacios en blanco

                if (codigo.isEmpty()) {
                    resultadoArea.setText("⚠️ Error: No ingresaste ninguna oración.");
                    return;
                }

                List<Token> tokens = Lexer.analizar(codigo);

                // Mostrar tokens encontrados
                StringBuilder resultado = new StringBuilder("🔍 Tokens generados:\n");
                for (Token token : tokens) {
                    resultado.append(token).append("\n");
                }

                // Validación sintáctica con BNF
                if (Parser.validarBNF(tokens)) {
                    resultado.append("\n✅ La oración sigue la gramática BNF y es válida.");
                } else {
                    resultado.append("\n❌ Error: La oración no cumple con la gramática BNF.");
                }

                resultadoArea.setText(resultado.toString());
            }
        });

        // Agregar componentes a la ventana
        frame.add(panelSuperior, BorderLayout.NORTH);
        frame.add(resultadoScroll, BorderLayout.CENTER);

        // Mostrar la ventana
        frame.setVisible(true);
    }

    // Clase Parser que analiza la estructura sintáctica según BNF
    static class Parser {
        public static boolean validarBNF(List<Token> tokens) {
            if (tokens.size() < 3) {
                System.out.println("❌ Error: La oración es demasiado corta.");
                return false;
            }

            if (!tokens.get(0).tipo.equals("Sujeto")) {
                System.out.println("❌ Error: La oración debe empezar con un sujeto.");
                return false;
            }

            if (!tokens.get(1).tipo.equals("Verbo")) {
                System.out.println("❌ Error: La segunda palabra debe ser un verbo.");
                return false;
            }

            if (!tokens.get(2).tipo.equals("Objeto")) {
                System.out.println("❌ Error: La tercera palabra debe ser un objeto válido.");
                return false;
            }

            return true;
        }

        // Clase del Analizador Semántico
        class SemanticAnalyzer {
            private static final HashMap<String, String> tablaSimbolos = new HashMap<>();

            // Verifica el significado del código
            public static boolean validarSemantica(List<Token> tokens) {
                if (tokens.isEmpty()) {
                    System.out.println("❌ Error: No hay tokens para analizar.");
                    return false;
                }

                // Verifica si la estructura sigue una regla semántica válida
                if (tokens.size() == 3 && tokens.get(0).tipo.equals("Sujeto") &&
                        tokens.get(1).tipo.equals("Verbo") && tokens.get(2).tipo.equals("Objeto")) {
                    System.out.println("✅ Validación semántica exitosa: La oración tiene sentido.");
                    return true;
                }

                // Manejo de variables (asignaciones)
                if (tokens.size() == 3 && tokens.get(0).tipo.equals("Sujeto") &&
                        tokens.get(1).tipo.equals("Asignación") && tokens.get(2).tipo.equals("Número")) {
                    tablaSimbolos.put(tokens.get(0).valor, tokens.get(2).valor);
                    System.out.println("✅ Variable asignada correctamente: " + tokens.get(0).valor + " = " + tokens.get(2).valor);
                    return true;
                }

                // Manejo de errores semánticos
                System.out.println("❌ Error semántico: La estructura no tiene un significado correcto.");
                return false;
            }
        }

    }
}