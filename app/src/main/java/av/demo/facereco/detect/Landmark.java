package av.demo.facereco.detect;

import android.graphics.Point;

/**
 * Created by Antonio Vitiello on 24/04/2018.
 */

public class Landmark {
    private Point[] points;
    private String label;

    private Landmark(Builder builder) {
        points = builder.points;
        label = builder.label;
    }

    public Point[] getPoints() {
        return points;
    }

    public String getLabel() {
        return label;
    }


    public static final class Builder {
        private Point[] points;
        private String label;

        public Builder() {
        }

        public Builder points(Point... points) {
            this.points = points;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Landmark build() {
            return new Landmark(this);
        }
    }
}
