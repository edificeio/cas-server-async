package fr.wseduc.cas.async;

public interface Handler<T> {

	void handle(T event);

}
