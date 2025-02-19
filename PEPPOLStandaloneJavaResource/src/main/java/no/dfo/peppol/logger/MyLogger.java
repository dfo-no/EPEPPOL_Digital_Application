package no.dfo.peppol.logger;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.text.SimpleDateFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MyLogger extends Handler {
    private TextArea logTextArea;
    
    public MyLogger(TextArea logTextArea) {
        this.logTextArea = logTextArea;
  
    }

    @Override
    public void publish(LogRecord record) {
        String message = record.getMessage();
        // Append the log message to the TextArea
       Platform.runLater( () -> logTextArea.appendText(record.getLevel()+" : "+message + "\n"));
    }

    @Override
    public void flush() {
        // No need to implement flushing for TextArea
    }

    @Override
    public void close() throws SecurityException {
        // No need to implement closing for TextArea
    }
}