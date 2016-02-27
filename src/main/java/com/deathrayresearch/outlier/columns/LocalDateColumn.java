package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.mapper.DateMapUtils;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.mintern.primitive.Primitive;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * A column in a base table that contains float values
 */
public class LocalDateColumn extends AbstractColumn implements DateMapUtils {

  public static final int MISSING_VALUE = (int) ColumnType.LOCAL_DATE.getMissingValue() ;
  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  private int[] data;

  private LocalDateColumn(String name) {
    super(name);
    data = new int[DEFAULT_ARRAY_SIZE];
  }

  private LocalDateColumn(String name, int initialSize) {
    super(name);
    data = new int[initialSize];
  }

  public int size() {
    return N;
  }

  @Override
  public ColumnType type() {
    return ColumnType.LOCAL_DATE;
  }

  @Override
  public boolean hasNext() {
    return pointer < N;
  }

  public int next() {
    return data[pointer++];
  }

  public void add(int f) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = f;
  }

  public void add(LocalDate f) {
    if (N >= data.length) {
      resize();
    }
    data[N++] = PackedLocalDate.pack(f);
  }

  // TODO(lwhite): Redo to reduce the increase for large columns
  private void resize() {
    int[] temp = new int[Math.round(data.length * 2)];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    int[] temp = new int[N + 100];
    System.arraycopy(data, 0, temp, 0, N);
    data = temp;
  }


  @Override
  public String getString(int row) {
    return PackedLocalDate.toDateString(data[row]);
  }

  @Override
  public LocalDateColumn emptyCopy() {
    return new LocalDateColumn(name());
  }

  @Override
  public void clear() {
    data = new int[DEFAULT_ARRAY_SIZE];
  }

  public void reset() {
    pointer = 0;
  }

  private LocalDateColumn copy() {
    LocalDateColumn copy = emptyCopy();
    copy.data = this.data;
    copy.N = this.N;
    return copy;
  }

  @Override
  public Column sortAscending() {
    LocalDateColumn copy = this.copy();
    Arrays.sort(copy.data);
    return copy;
  }

  @Override
  public Column sortDescending() {
    LocalDateColumn copy = this.copy();
    Primitive.sort(copy.data, (d1, d2) -> Integer.compare(d2, d1), false);
    return copy;
  }


  @Override
  public int countUnique() {
    IntSet ints = new IntOpenHashSet(data.length);
    for (int i : data) {
      ints.add(i);
    }
    return ints.size();
  }

  public LocalDate firstElement() {
    if (isEmpty()) {
      return null;
    }
    return PackedLocalDate.asLocalDate(data[0]);
  }

  public LocalDate max() {
    int max;
    int missing = Integer.MIN_VALUE;
    if (!isEmpty()) {
      max = data[0];
    } else {
      return null;
    }
    for (int aData : data) {
      if (missing != aData) {
        max = (max > aData) ? max : aData;
      }
    }

    if (missing == max) {
      return null;
    }
    return PackedLocalDate.asLocalDate(max);
  }

  public LocalDate min() {
    int min;
    int missing = Integer.MIN_VALUE;

    if (!isEmpty()) {
      min = data[0];
    } else {
      return null;
    }
    for (int aData : data) {
      if (missing != aData) {
        min = (min < aData) ? min : aData;
      }
    }
    if (Integer.MIN_VALUE == min) {
      return null;
    }
    return PackedLocalDate.asLocalDate(min);
  }

  public CategoryColumn dayOfWeek() {
    CategoryColumn newColumn = CategoryColumn.create(this.name() + " day of week");
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.set(r, null);
      } else {
        newColumn.set(r, PackedLocalDate.getDayOfWeek(c1).toString());
      }
    }
    return newColumn;
  }

  public IntColumn dayOfMonth() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of month");
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDate.getDayOfMonth(c1));
      }
    }
    return newColumn;
  }

  public IntColumn dayOfYear() {
    IntColumn newColumn = IntColumn.create(this.name() + " day of month");
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDate.getDayOfYear(c1));
      }
    }
    return newColumn;
  }

  public IntColumn month() {
    IntColumn newColumn = IntColumn.create(this.name() + " month");
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      if (c1 == LocalDateColumn.MISSING_VALUE) {
        newColumn.add(IntColumn.MISSING_VALUE);
      } else {
        newColumn.add(PackedLocalDate.getMonthValue(c1));
      }
    }
    return newColumn;
  }


  public LocalDate get(int index) {
    return PackedLocalDate.asLocalDate(data[index]);
  }


  public static LocalDateColumn create(String name) {
    return new LocalDateColumn(name);
  }

  @Override
  public boolean isEmpty() {
    return N == 0;
  }

  @Override
  public Comparator<Integer> rowComparator() {
    return comparator;
  }

  Comparator<Integer> comparator = new Comparator<Integer>() {

    @Override
    public int compare(Integer r1, Integer r2) {
      int f1 = data[r1];
      int f2 = data[r2];
      return Integer.compare(f1, f2);
    }
  };

  public static LocalDateColumn create(String fileName, IntArrayList dates) {
    LocalDateColumn column = new LocalDateColumn(fileName, dates.size());
    column.data = dates.elements();
    return column;
  }

  public int convert(String value) {
    if (Strings.isNullOrEmpty(value)
        || TypeUtils.MISSING_INDICATORS.contains(value)
        || value.equals("-1")) {
      return (int) ColumnType.LOCAL_DATE.getMissingValue();
    }
    value = Strings.padStart(value, 4, '0');
    return PackedLocalDate.pack(LocalDate.parse(value, TypeUtils.DATE_FORMATTER));
  }

  public void addCell(String string) {
    try {
      add(convert(string));
    } catch (NullPointerException e) {
      throw new RuntimeException(name() + ": "
          + string + ": "
          + e.getMessage());
    }
  }

  public int getInt(int index) {
    return data[index];
  }

  public RoaringBitmap isEqualTo(LocalDate value) {
    RoaringBitmap results = new RoaringBitmap();
    int packedLocalDate = PackedLocalDate.pack(value);
    int i = 0;
    while (hasNext()) {
      if (packedLocalDate == next()) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  /**
   * Returns a table of dates and the number of observations of those dates
   */
  public View summary() {

    Object2IntMap<LocalDate> counts = new Object2IntOpenHashMap<>();

    for (int i = 0; i < N; i++) {
      LocalDate value;
      int next = data[i];
      if (next == Integer.MIN_VALUE) {
        value = null;
      } else {
        value = PackedLocalDate.asLocalDate(next);
      }
      if (counts.containsKey(value)) {
        counts.put(value, counts.get(value) + 1);
      } else {
        counts.put(value, 1);
      }
    }

    Table table = new Table(name());
    table.addColumn(LocalDateColumn.create("Date"));
    table.addColumn(IntColumn.create("Count"));

    for (Map.Entry<LocalDate, Integer> entry : counts.object2IntEntrySet()) {
      //Row row = table.newRow();
      table.localDateColumn(0).add(entry.getKey());
      table.intColumn(1).add(entry.getValue());
    }
    table = table.sortDescendingOn("Count");

    return table.head(5);
  }

  public LocalDateTimeColumn atTime(LocalTimeColumn c) {
    LocalDateTimeColumn newColumn = LocalDateTimeColumn.create(this.name() + " " + c.name());
    for (int r = 0; r < this.size(); r++) {
      int c1 = this.getInt(r);
      int c2 = c.get(r);
      if (c1 == MISSING_VALUE || c2 == LocalTimeColumn.MISSING_VALUE) {
        newColumn.add(LocalDateTimeColumn.MISSING_VALUE);
      } else {
        LocalDate value1 = PackedLocalDate.asLocalDate(c1);
        LocalTime time = PackedLocalTime.asLocalTime(c2);
        newColumn.add(PackedLocalDateTime.pack(value1, time));
      }
    }
    return newColumn;
  }

  public RoaringBitmap isInQ1() {

    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      int next = next();
      if (PackedLocalDate.isInQ1(next)) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isInQ2() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      int next = next();
      if (PackedLocalDate.isInQ2(next)) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isInQ3() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      int next = next();
      if (PackedLocalDate.isInQ3(next)) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public RoaringBitmap isInQ4() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      int next = next();
      if (PackedLocalDate.isInQ4(next)) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }
}