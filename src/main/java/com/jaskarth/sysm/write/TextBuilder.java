package com.jaskarth.sysm.write;

public class TextBuilder implements Appendable, CharSequence {
    private final StringBuilder sb = new StringBuilder();

    public TextBuilder indent(int amt) {
        sb.append("\t".repeat(amt));

        return this;
    }

    public TextBuilder sp() {
        sb.append(" ");

        return this;
    }

    public TextBuilder sc() {
        sb.append(";");

        return this;
    }

    public TextBuilder ln() {
        sb.append("\n");

        return this;
    }

    public TextBuilder scln() {
        sc();
        ln();

        return this;
    }

    public TextBuilder append(boolean b) {
        sb.append(b);
        return this;
    }

    public TextBuilder append(int i) {
        sb.append(i);
        return this;
    }

    public TextBuilder append(long lng) {
        sb.append(lng);
        return this;
    }

    public TextBuilder append(float f) {
        sb.append(f);
        return this;
    }

    public TextBuilder append(double d) {
        sb.append(d);
        return this;
    }

    public TextBuilder append(Object o) {
        if (o == null) {
            throw new NullPointerException("Should not be null");
        }

        sb.append(o.toString());
        return this;
    }

    public TextBuilder appendStr(CharSequence csq) {
        sb.append("\"");
        sb.append(csq);
        sb.append("\"");
        return this;
    }

    // Appendable

    @Override
    public TextBuilder append(CharSequence csq) {
        sb.append(csq);
        return this;
    }

    @Override
    public TextBuilder append(CharSequence csq, int start, int end) {
        sb.append(csq, start, end);
        return this;
    }

    @Override
    public TextBuilder append(char c) {
        sb.append(c);
        return this;
    }

    // CharSequence

    @Override
    public int length() {
        return sb.length();
    }

    @Override
    public char charAt(int index) {
        return sb.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return sb.subSequence(start, end);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
