package cinema.utils;

//а дальше по идее надо сделать свой ORM-фреймворк типа sql2o
//аннотациями отмечаем колонки в каждом классе, какой колонке в СУБД каждый член класса соответствует
//затем через рефлексию проходимся по классу и собираем запрос
//что-то подобное я бы делал в 1С
public interface Mapper<T, V> {
    T findById(V id) throws Exception;
    void save(T object) throws Exception;
    void delete(T object) throws Exception;
}