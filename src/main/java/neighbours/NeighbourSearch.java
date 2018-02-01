package neighbours;

import com.google.common.collect.MinMaxPriorityQueue;

import java.util.*;

/**
 * A implementation for k nearest neighbours search
 *
 * @author Cedric Richter
 */
public class NeighbourSearch extends AbstractSet<INamedObject> {

    private IStringMeasure measure;

    private Set<INamedObject> collection = new HashSet<>();

    public NeighbourSearch(IStringMeasure measure) {
        this.measure = measure;
    }

    /**
     * Gets the k nearest neighbours together with the distance to the point
     * @param key search key
     * @param k number of neighbours
     * @return list of neighbours with distance
     */
    public List<DistantObject> getNNDistant(String key, int k){
        MinMaxPriorityQueue<DistantObject> queue = MinMaxPriorityQueue.maximumSize(k).create();

        for(INamedObject o: collection){
            double min = queue.isEmpty()?1.0:queue.peekLast().distant;
            if(measure.inFilter(key, o.getName(), min)){
                double d = measure.getDistance(key, o.getName());
                queue.add(new DistantObject(o, d));
            }
        }

        List<DistantObject> objs = new ArrayList<>();
        while(!queue.isEmpty())
            objs.add(queue.pollFirst());

        return objs;
    }

    /**
     * Gets the k nearest neighbours
     * @param key search key
     * @param k number of neighbours
     * @return list of neighbours
     */
    public List<INamedObject> getNearestNeighbour(String key, int k){
        List<INamedObject> objs = new ArrayList<>();

        for(DistantObject obj: getNNDistant(key, k))
            objs.add(obj.content);

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


    public class DistantObject implements Comparable<DistantObject>{

        private INamedObject content;

        private double distant;

        public DistantObject(INamedObject content, double distant) {
            this.content = content;
            this.distant = distant;
        }

        public INamedObject getContent() {
            return content;
        }

        public double getDistant() {
            return distant;
        }

        @Override
        public int compareTo(DistantObject o) {
            return (int)Math.signum(this.distant - o.distant);
        }
    }



}
