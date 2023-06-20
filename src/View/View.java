package View;

public interface View {

    default void printString (String s){
        System.out.printf("%s\n",s);
    }

    default void printString() {
        System.out.println();
    }
    default void printWithoutNewLine (String s) {
        System.out.print(s);
    }

    default void clearTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
