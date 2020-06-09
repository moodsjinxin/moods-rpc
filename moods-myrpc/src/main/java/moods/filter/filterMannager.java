package moods.filter;

public class filterMannager {
    public static AbstractBeforeFilter beforeFilter;
    public static AbstractAfterFilter afterFilter;

    public static AbstractBeforeFilter tailBeforeFilter;
    public static AbstractAfterFilter tailAfterFilter;

    public static void addBeforeFilter(AbstractBeforeFilter filter){
        if (beforeFilter == null) {
            beforeFilter = filter;
            tailBeforeFilter = filter;
        } else {
            tailBeforeFilter.next = filter;
            tailBeforeFilter = tailBeforeFilter.next;
        }
    }


    public static void addAfterFilter(AbstractAfterFilter filter) {
        if (afterFilter == null) {
            afterFilter = filter;
            tailAfterFilter = filter;
        } else {
            tailAfterFilter.next = filter;
            tailAfterFilter = tailAfterFilter.next;
        }
    }
}
