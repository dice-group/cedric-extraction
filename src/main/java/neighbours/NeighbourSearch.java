package neighbours;

import com.google.common.collect.MinMaxPriorityQueue;

import java.util.*;

public class NeighbourSearch extends AbstractSet<INamedObject> {

    private IStringMeasure measure;

    private Set<INamedObject> collection = new HashSet<>();

    public NeighbourSearch(IStringMeasure measure) {
        this.measure = measure;
    }

    public List<INamedObject> getNearestNeighbour(String key, int k){
        MinMaxPriorityQueue<DistantObject> queue = MinMaxPriorityQueue.maximumSize(k).create();

        for(INamedObject o: collection){
            double min = queue.peekLast().distant;
            if(measure.inFilter(key, o.getName(), min)){
                double d = measure.getDistance(key, o.getName());
                queue.add(new DistantObject(o, d));
            }
        }

        List<INamedObject> objs = new ArrayList<>();
        while(!queue.isEmpty())
            objs.add(queue.pollFirst().content);

        return objs;
    }

    @Override
    public boolean add(INamedObject obj){
        return collection.add(obj);
    }

    @Override
    public boolean contains(Object o) {
        return collection.contains(o);
    }

    @Override
    public boolean remove(Object o) {
        return collection.remove(o);
    }

    @Override
    public Iterator<INamedObject> iterator() {
        return collection.iterator();
    }

    @Override
    public int size() {
        return collection.size();
    }


    private class DistantObject implements Comparable<DistantObject>{

        private INamedObject content;
        private double distant;

        public DistantObject(INamedObject content, double distant) {
            this.content = content;
            this.distant = distant;
        }

        @Override
        public int compareTo(DistantObject o) {
            return (int)Math.signum(this.distant - o.distant);
        }
    }



}
