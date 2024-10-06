package com.modrinth.methane.util;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector4i;

public class Helpers {

    /**
     * Minecraft uses a system where y is down, and x is right when working with screen coordinates.
     * Imagine a box as such:
     *  where p = point,
     * xy_______
     * |       |
     * |   p   |
     * |______zw
     *
     * if p is within the bounds, it inherently must be greater than x and y, and less than z and w.
     *
     * @param point x and y coordinate of point to test for
     * @param x the leftmost coordinate
     * @param y the uppermost coordinate
     * @param z the rightmost coordinate
     * @param w the lowest coordinate
     * @return whether this point is within the bounds.
     */
    public static boolean withinBounds(Vector2d point, float x, float y, float z, float w){
        return point.x > x && point.y > y && point.x < z && point.y < w;
    }

    /**
     * @see #withinBounds(Vector2d, float, float, float, float) 
     */
    public static boolean withinBounds(Vector2d point, Vector4i bounds){
        return point.x > bounds.x && point.y > bounds.y && point.x < bounds.z && point.y < bounds.w;
    }

    /**
     * @see #withinBounds(Vector2d, float, float, float, float)
     */
    public static boolean withinBounds(Vector2i point, Vector4i bounds){
        return point.x > bounds.x && point.y > bounds.y && point.x < bounds.z && point.y < bounds.w;
    }

}
