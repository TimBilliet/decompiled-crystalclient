package co.crystaldev.client.feature.base;

import co.crystaldev.client.feature.annotations.properties.DropdownMenu;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Dropdown<T> {
  private transient T[] values;

  private transient List<T> defaultValues;

  private transient int maxSelections;

  private transient boolean limitlessSelections;

  private transient BiConsumer<Dropdown<T>, T> onSelect;

  public String toString() {
    return "Dropdown(values=" + Arrays.deepToString((Object[]) getValues()) + ", defaultValues=" + getDefaultValues() + ", maxSelections=" + getMaxSelections() + ", limitlessSelections=" + isLimitlessSelections() + ", onSelect=" + this.onSelect + ", currentlySelected=" + getCurrentlySelected() + ")";
  }

  public T[] getValues() {
    return this.values;
  }

  public List<T> getDefaultValues() {
    return this.defaultValues;
  }

  public int getMaxSelections() {
    return this.maxSelections;
  }

  public boolean isLimitlessSelections() {
    return this.limitlessSelections;
  }

  public void setOnSelect(BiConsumer<Dropdown<T>, T> onSelect) {
    this.onSelect = onSelect;
  }

  @SerializedName("currentlySelected")
  private LinkedList<T> currentlySelected = new LinkedList<>();

  public LinkedList<T> getCurrentlySelected() {
    return this.currentlySelected;
  }

  public Dropdown(T[] values, T[] defaultValues, int maxSelections, boolean limitlessSelections) {
    this.values = values;
    this.defaultValues = Arrays.asList(defaultValues);
    this.maxSelections = maxSelections;
    this.limitlessSelections = limitlessSelections;
  }

  public Dropdown(T[] values, T[] defaultValues) {
    this(values, defaultValues, 1, false);
  }

  public Dropdown(T[] values, T[] defaultValues, int maxSelections) {
    this(values, defaultValues, maxSelections, false);
  }

  public Dropdown(T[] values, T[] defaultValues, boolean limitlessSelections) {
    this(values, defaultValues, 1, limitlessSelections);
  }

  public Dropdown(DropdownMenu menu) {
    this((T[]) menu.values(), (T[]) menu.defaultValues(), menu.maximumSelections(), menu.limitlessSelections());
  }

  public boolean isMultiSelect() {
    return (this.limitlessSelections || this.maxSelections > 1);
  }

  public boolean isEmpty() {
    return this.currentlySelected.isEmpty();
  }

  public boolean select(T value) {
    boolean flag = false;
    for (T v : this.values) {
      if (v.equals(value)) {
        flag = true;
        break;
      }
    }
    if (!flag)
      return false;
    if (this.currentlySelected.contains(value)) {
      if (!this.limitlessSelections && this.maxSelections == 1 && this.currentlySelected.getFirst().equals(value))
        return false;
      return !this.currentlySelected.remove(value);
    }
    if (!this.limitlessSelections && this.currentlySelected.size() >= this.maxSelections)
      if (this.maxSelections == 1) {
        this.currentlySelected.clear();
      } else {
        while (this.currentlySelected.size() >= this.maxSelections)
          this.currentlySelected.removeFirst();
      }
    if (this.onSelect != null)
      this.onSelect.accept(this, value);
    return this.currentlySelected.add(value);
  }

  public void setDefault() {
    this.currentlySelected.clear();
    this.currentlySelected.addAll(this.defaultValues);
  }

  public void setCurrentlySelected(List<T> selected) {
    this.currentlySelected.clear();
    this.currentlySelected.addAll((Collection<? extends T>) selected.stream().filter(str -> {
      for (T value : this.values) {
        if (value.equals(str))
          return true;
      }
      return false;
    }).collect(Collectors.toList()));
  }

  public void copy(DropdownMenu menu) {
    this.values = (T[]) menu.values();
    this.defaultValues = Arrays.asList((T[]) menu.defaultValues());
    this.limitlessSelections = menu.limitlessSelections();
    this.maxSelections = menu.maximumSelections();
  }

  public T getCurrentSelection() {
    if (isMultiSelect())
      throw new RuntimeException("Dropdown must not have multi-selection capability");
    return this.currentlySelected.isEmpty() ? null : this.currentlySelected.getFirst();
  }
  public boolean isSelected(T value) {
    return this.currentlySelected.contains(value);
  }
}
  
