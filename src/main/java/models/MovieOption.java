package models;

import java.util.Objects;

public class MovieOption {
    private String name;

    public MovieOption(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "models.MovieOption{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieOption that = (MovieOption) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
