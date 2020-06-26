package com.google.sps.data;

public class AnimalHistory {
    private int year;
    private String category;

    private AnimalHistory();

    private AnimalHistory(int year, String category) {
        this.year = year;
        this.category = category;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setCategory(String code) {
        this.category = category;
    }

    public int getYear() {
        return year;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public int hashCode() {
        int hash = year;
        final int prime = 31;

        hash = hash * prime + category.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof AnimalHistory)) {
            return false;
        }

        AnimalHistory a = (AnimalHistory) o;

        boolean yearEqual = (this.year == a.getYear());
        boolean categoryEqual = this.category.equals(a.getCategory());
        return yearEqual && categoryEqual;
    }
}