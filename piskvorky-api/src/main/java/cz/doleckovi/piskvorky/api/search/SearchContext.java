package cz.doleckovi.piskvorky.api.search;

/** Search context.
 * <p>Search context represents state of search. It provides search criteria, can hold information about search
 * progress, coordinate parallel branches of multithreaded search and allow observation of search progress or its
 * termination from external thread.</p>
 */
public interface SearchContext {

	/** Gets target depth of the search.
	 * @return Target depth
	 */
	int targetDepth();

}
