package ru.job4j.tracker.store;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.tracker.model.Item;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class SqlTrackerTest {

  private static Connection connection;

  @BeforeClass
  public static void initConnection() {
    try (InputStream in = SqlTrackerTest.class.getClassLoader().getResourceAsStream("test.properties")) {
      Properties config = new Properties();
      config.load(in);
      Class.forName(config.getProperty("driver-class-name"));
      connection = DriverManager.getConnection(
              config.getProperty("url"),
              config.getProperty("username"),
              config.getProperty("password")

      );
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @AfterClass
  public static void closeConnection() throws SQLException {
    connection.close();
  }

  @After
  public void wipeTable() throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement("delete from items")) {
      statement.execute();
    }
  }

  @Test
  public void whenSaveItemAndFindByGeneratedIdThenMustBeTheSame() {
    SqlTracker tracker = new SqlTracker(connection);
    Item item = tracker.add(new Item("item"));
    assertThat(tracker.findById(item.getId()), is(item));
  }

  @Test
  public void whenEditItemThenMustBeTheSameNameAndDateAsNew() {
    SqlTracker tracker = new SqlTracker(connection);
    Item item = tracker.add(new Item("item"));
    Item itemToCheck = new Item("itemToCheck");
    tracker.replace(item.getId(), itemToCheck);
    Item foundItem = tracker.findById(item.getId());
    assertThat(foundItem.getName(), is(itemToCheck.getName()));
    assertThat(foundItem.getLocalDateTime().withNano(0), is(itemToCheck.getLocalDateTime().withNano(0)));
  }

  @Test
  public void whenDeleteItemAndFindByIdDeletedItemThenMustBeNull() {
    SqlTracker tracker = new SqlTracker(connection);
    Item item = tracker.add(new Item("item"));
    int itemId = item.getId();
    tracker.delete(itemId);
    assertNull(tracker.findById(itemId));
  }

  @Test
  public void whenSaveAndFindByNameThenListMustContainAddedItem() {
    SqlTracker tracker = new SqlTracker(connection);
    Item item = tracker.add(new Item("item"));
    assertThat(tracker.findByName(item.getName()), is(List.of(item)));
  }

  @Test
  public void whenSaveThenAllItemsMustBeInList() {
    SqlTracker tracker = new SqlTracker(connection);
    Item item = tracker.add(new Item("itemName"));
    Item itemTwo = tracker.add(new Item("itemNameTwo"));
    Item itemThree = tracker.add(new Item("itemNameThree"));
    assertThat(tracker.findAll(), is(List.of(item, itemTwo, itemThree)));
  }
}