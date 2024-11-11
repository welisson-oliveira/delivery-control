package com.acert.deliverycontrol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class DesignPatternCommand {
    public static void main(final String[] args) {
        final Editor editor1 = new Editor("teste");
        final Editor editor2 = new Editor("");
        final Application application = new Application();

        System.out.println();
        System.out.println("Editor1: " + editor1.getSelection());
        System.out.println("Editor2: " + editor2.getSelection());
        System.out.println("App: " + application.clipboard());

        application.executeCommand(application.copy(editor1));
        application.executeCommand(application.paste(editor2));

        System.out.println();
        System.out.println("Editor1: " + editor1.getSelection());
        System.out.println("Editor2: " + editor2.getSelection());
        System.out.println("App: " + application.clipboard());

        application.executeUndo();
        application.executeUndo();

        System.out.println();
        System.out.println("Editor1: " + editor1.getSelection());
        System.out.println("Editor2: " + editor2.getSelection());
        System.out.println("App: " + application.clipboard());

    }
}

@Getter
@Setter
@AllArgsConstructor
class Editor {
    private String text;

    /**
     * retorna o texto selecionado
     *
     * @return text
     */
    public String getSelection() {
        return this.text;
    }

    /**
     * deleta o texto selecionado
     */
    public void deleteSelection() {
        this.text = "";
    }

    /**
     * Insere o conteudo da area de transferencia na posição atual
     *
     * @param text
     */
    public void replaceSelection(final String text) {
        this.text = text;
    }
}

class Application {
    private String clipboard;
    private final CommandHistory history = new CommandHistory();

    public Command copy(final Editor editor) {
        return new CopyCommand(this, editor);
    }

    public Command cut(final Editor editor) {
        return new CutCommand(this, editor);
    }

    public Command paste(final Editor editor) {
        return new PasteCommand(this, editor);
    }

    public Command undo(final Editor editor) {
        return new UndoCommand(this, editor);
    }

    public void executeCommand(final Command command) {
        command.execute();
        this.history.push(command);
    }

    public void executeUndo() {
        final var command = this.history.pop();
        command.undo();
    }

    public void clipboard(final String clipboard) {
        this.clipboard = clipboard;
    }

    public String clipboard() {
        return this.clipboard;
    }


}

@RequiredArgsConstructor
abstract class Command {
    protected final Application application;
    protected final Editor editor;
    protected String backup;


    public void saveBackup() {
        this.backup = this.editor.getText();
    }

    public void undo() {
        this.editor.setText(this.backup);
    }

    public abstract void execute();
}

class CopyCommand extends Command {

    public CopyCommand(final Application application, final Editor editor) {
        super(application, editor);
    }

    @Override
    public void execute() {
        this.saveBackup();
        this.application.clipboard(this.editor.getSelection());
    }
}

class CutCommand extends Command {

    public CutCommand(final Application application, final Editor editor) {
        super(application, editor);
    }

    @Override
    public void execute() {
        this.saveBackup();
        this.application.clipboard(this.editor.getSelection());
        this.editor.deleteSelection();
    }
}

class PasteCommand extends Command {

    public PasteCommand(final Application application, final Editor editor) {
        super(application, editor);
    }

    @Override
    public void execute() {
        this.saveBackup();
        this.editor.replaceSelection(this.application.clipboard());
    }
}

class UndoCommand extends Command {

    public UndoCommand(final Application application, final Editor editor) {
        super(application, editor);
    }

    @Override
    public void execute() {
        this.application.undo(this.editor);
    }
}

class CommandHistory {
    private final List<Command> history = new ArrayList<>();

    public void push(final Command command) {
        this.history.add(command);
    }

    public Command pop() {
        final Command command = this.history.get(this.history.size() - 1);
        this.history.remove(command);
        return command;
    }
}


