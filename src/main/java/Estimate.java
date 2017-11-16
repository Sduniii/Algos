public class Estimate {
    private double start;
    private double end;
    private long timeStart;
    private long timeEnd;
    private long estimate;

    public Estimate() {
        start = 0;
        end = 0;
        timeEnd = 0;
        timeStart = 0;
        estimate = 0;
    }


    private double getDifference() {
        return end - start;
    }

    private long getTimeDifference() {
        return timeEnd - timeStart;
    }

    public long getEstimate(){
        return estimate;
    }

    public void calcEstimate() {
        estimate =  (long) (100 / getDifference() * getTimeDifference());
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }
}
