package com.vincentmet.customquests.gui.editor;

import java.util.LinkedList;

public class ActionQueue{
    private final LinkedList<Runnable> actions = new LinkedList<>();
    
    public void push(Runnable action){
        actions.add(action);
    }
    
    public void execute(){
        while(actions.size()>0){
            actions.getFirst().run();
            actions.pop();
        }
    }
}