package ru.job4j.tracker.action;

import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.output.Output;
import ru.job4j.tracker.store.Store;

public class CreateManyAction implements UserAction {

  private final Output out;

  public CreateManyAction(Output out) {
    this.out = out;
  }

  @Override
  public String name() {
    return "=== Create many items ====";
  }

  @Override
  public boolean execute(Input input, Store tracker) {
    String name = input.askStr("Enter name: ");
    Item item = new Item(name);
    for (int i = 0; i < 500000; i++) {
      tracker.add(item);
    }
    return true;
  }
}
