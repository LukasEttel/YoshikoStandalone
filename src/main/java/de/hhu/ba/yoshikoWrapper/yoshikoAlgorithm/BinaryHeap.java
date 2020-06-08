package de.hhu.ba.yoshikoWrapper.yoshikoAlgorithm;

import java.util.*;

public class BinaryHeap {
    ArrayList<Double> list = new ArrayList();
    HashMap<Integer, YoshikoEdge> map = new HashMap<>();
    HashMap<YoshikoEdge, Integer> reverseMap = new HashMap<>();
    int k = -1;

    public BinaryHeap(){
        list.add(Double.NEGATIVE_INFINITY);
        map.put(0, null);
        reverseMap.put(null, 0);
    }

    public void addMaxIcfIcp(YoshikoEdge edge){

        list.add(edge.maxIcfIcp);
        map.put(list.size()-1, edge);
        reverseMap.put(edge, list.size()-1);

        checkParent(list.size()-1);

        if (list.size()-1 == 30864){
            System.out.println("asd");
        }
    }

    public void addIcfMinusIcp(YoshikoEdge edge){
        list.add(edge.icfMinusIcp);
        map.put(list.size()-1, edge);

        reverseMap.put(edge, list.size()-1);

        checkParent(list.size()-1);
    }

    public void remove(YoshikoEdge edge){
        if (!reverseMap.containsKey(edge)){
            return;
        }
        int index = reverseMap.get(edge);

        if (edge.weight != Double.NEGATIVE_INFINITY){
            //System.out.println("MELDUNG 5");
        }

        if (index < 0){
            //Die Kante wurde bereits auf "forbidden" gesetzt
            return;
        }

        saveRemove(index, edge);
    }

    public YoshikoEdge popMax(){
        double maxVal = Double.NEGATIVE_INFINITY;
        for (Double i : list){
            if (i > maxVal){
                maxVal = i;
            }
        }

        int index = list.indexOf(maxVal);

        YoshikoEdge e = map.get(index);

        if (e == null){
            System.out.println(2);
        }

        saveRemove(index, e);

        return e;
    }

    private void saveRemove(int index, YoshikoEdge e) {
        map.replace(index, map.get(map.size()-1));
        map.remove(map.size()-1);

        YoshikoEdge movedEdge = map.get(index);

        reverseMap.replace(movedEdge, index);
        reverseMap.remove(e);

        list.set(index, list.get(list.size()-1));
        list.remove(list.size()-1);
    }


    public void updateEdge(YoshikoEdge edge){
        if (!reverseMap.containsKey(edge)){
            return;
        }
        int index = reverseMap.get(edge);

        if (index < 0){
            return;
        }

        if (k<0){
            list.set(index, edge.maxIcfIcp);
        }else {
            list.set(index, edge.icfMinusIcp);
        }

        checkParent(index);
        checkChild(index);
    }


    public int size(){
        return list.size()-1;
    }

    private void checkParent(int index){
        int parentIndex = index / 2;
        if (list.get(index) < list.get(parentIndex)){
            swap(index, parentIndex);

            checkParent(parentIndex);
        }
    }

    private void checkChild(int index){
        int childIndex = index * 2;
        if (list.size()-1 < childIndex){
            return;
        }else if(list.size()-1 == childIndex){

        }else if (list.get(childIndex) < list.get(childIndex+1)){
            childIndex++;
        }

        if (list.get(index) > list.get(childIndex)){
            swap(index, childIndex);

            checkChild(childIndex);
        }
    }

    private void swap(int index, int parentIndex){
        double tmpVal = list.get(index);
        list.set(index, list.get(parentIndex));
        list.set(parentIndex, tmpVal);


        YoshikoEdge edge1 = map.get(index);
        YoshikoEdge edge2 = map.get(parentIndex);

        Integer index1 = reverseMap.get(edge1);
        Integer index2 = reverseMap.get(edge2);

        map.replace(index, edge2);
        map.replace(parentIndex, edge1);

        reverseMap.replace(edge1, index2);
        reverseMap.replace(edge2, index1);
    }
}