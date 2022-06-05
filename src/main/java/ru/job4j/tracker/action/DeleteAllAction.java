package ru.job4j.tracker.action;

import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.output.Output;
import ru.job4j.tracker.store.Store;

import java.util.List;

public class DeleteAllAction implements UserAction {

  private final Output out;

  public DeleteAllAction(Output out) {
    this.out = out;
  }

  @Override
  public String name() {
    return "=== Delete all items ====";
  }

  @Override
  public boolean execute(Input input, Store tracker) {
    List<Item> itemList = tracker.findAll();
    itemList.forEach(item -> tracker.delete(item.getId()));
    out.println("All items deleted");
    return true;
  }
}
