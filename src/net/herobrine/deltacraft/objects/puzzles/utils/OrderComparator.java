package net.herobrine.deltacraft.objects.puzzles.utils;

import net.herobrine.deltacraft.objects.inventory.Order;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
       int seq1 = o1.getCar().getSequence();
       int seq2 = o2.getCar().getSequence();
       return Integer.compare(seq1, seq2);
    }
}
