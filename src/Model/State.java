package Model;

import java.io.*;

public class State {
    private String fileOut;
    private String fileIn;

    public State(){
        this.fileIn = "AppState.dat";
        this.fileOut = "AppState.dat";
    }

    public State (String fileIn, String fileOut) {
        this.fileIn = fileIn;
        this.fileOut = fileOut;
    }
    public void saveData (AppModel appModel) throws FileNotFoundException, IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(this.fileOut);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(appModel);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public AppModel loadData() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(this.fileIn);
        ObjectInputStream objectOutputStream = new ObjectInputStream(fileInputStream);
        AppModel appModel = (AppModel) objectOutputStream.readObject();
        objectOutputStream.close();
        return appModel;
    }

    public String getFileOut() {
        return fileOut;
    }

    public void setFileOut(String fileOut) {
        this.fileOut = fileOut;
    }

    public String getFileIn() {
        return fileIn;
    }

    public void setFileIn(String fileIn) {
        this.fileIn = fileIn;
    }
}
