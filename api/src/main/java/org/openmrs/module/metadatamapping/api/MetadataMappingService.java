/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.metadatamapping.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.module.metadatamapping.MetadataMapping;
import org.openmrs.module.metadatamapping.MetadataSet;
import org.openmrs.module.metadatamapping.MetadataSetMember;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.MetadataTermMapping;
import org.openmrs.module.metadatamapping.RetiredHandlingMode;
import org.openmrs.module.metadatamapping.api.exception.InvalidMetadataTypeException;

/**
 * The service.
 */
public interface MetadataMappingService {
	
	/**
	 * Creates a local concept source from the implementation Id.
	 * <p>
	 * The local source is in a format 'implementationId-dict'. The '-dict' postfix is defined in
	 * {@link MetadataMapping#LOCAL_SOURCE_NAME_POSTFIX}.
	 * 
	 * @return the local source
	 * @throws APIException if the local source could not be created
	 */
	ConceptSource createLocalSourceFromImplementationId();
	
	/**
	 * Returns a configured local concept source.
	 * <p>
	 * The local source is read from the {@link MetadataMapping#GP_LOCAL_SOURCE_UUID} global
	 * property.
	 * 
	 * @return the local source
	 * @throws APIException if the local source is not configured
	 * @should return local source if gp set
	 * @should fail if gp is not set
	 */
	ConceptSource getLocalSource();
	
	/**
	 * Returns true if local source is configured.
	 * 
	 * @return true if configured
	 */
	boolean isLocalSourceConfigured();
	
	/**
	 * Returns true if local mappings should be added on export.
	 * 
	 * @return true if should add local mappings
	 */
	boolean isAddLocalMappingOnExport();
	
	/**
	 * Adds local mapping to the given concept.
	 * <p>
	 * A mapping in a format 'localSource:concetpId' is added to a concept if there is no other
	 * mapping to the local source in the concept.
	 * <p>
	 * The concept is saved at the end.
	 * <p>
	 * It delegates to
	 * {@link ConceptAdapter#addMapping(Concept, ConceptSource, String)}
	 * 
	 * @param concept concept to map
	 * @throws APIException if the local source is not configured
	 * @should add mapping if not found
	 * @should not add mapping if found
	 * @should fail if local source not configured
	 */
	void addLocalMappingToConcept(Concept concept);
	
	/**
	 * Adds local mappings to all concepts in the system.
	 * <p>
	 * It iterates over all concept and calls {@link #addLocalMappingToConcept(Concept)}.
	 * 
	 * @throws APIException reserved for future use
	 * @should delegate for all concepts
	 */
	void addLocalMappingToAllConcepts();
	
	/**
	 * Returns sources to which you are subscribed.
	 * 
	 * @return the set of sources or the empty set if nothing found
	 * @throws APIException reserved for future use
	 * @should return set if gp defined
	 * @should return empty set if gp not defined
	 */
	Set<ConceptSource> getSubscribedSources();
	
	/**
	 * Adds the given source to the subscribed sources list.
	 * 
	 * @param conceptSource source to add
	 * @return true if added or false if already there
	 * @should add subscribed source
	 * @should return false if subscribed source present
	 */
	boolean addSubscribedSource(ConceptSource conceptSource);
	
	/**
	 * Removes the given source from the subscribed sources list.
	 * 
	 * @param conceptSource source to remove
	 * @return true if removed or false if not present
	 * @should remove subscribed source
	 * @should return false if subscribed source not present
	 */
	boolean removeSubscribedSource(ConceptSource conceptSource);
	
	/**
	 * Determines if the given concept is local.
	 * <p>
	 * A concept is local if it does not contain a source returned by
	 * {@link #getSubscribedSources()}.
	 * 
	 * @param concept concept to check
	 * @return true if local
	 * @throws APIException reserved for future use
	 * @should return true if local
	 * @should return false if not local
	 */
	boolean isLocalConcept(Concept concept);
	
	/**
	 * Returns a concept by mapping in a format (1) 'source:code' or (2) 'conceptId'.
	 * <p>
	 * It delegates to {@link ConceptService#getConceptByMapping(String, String)} in case (1) and to
	 * {@link #getConcept(Integer)} in case (2).
	 * 
	 * @param mapping mapping or identifier of the concept
	 * @return the concept or null if not found
	 * @throws APIException reserved for future use
	 * @should return non retired if retired also found by mapping
	 * @should return retired if no other found by mapping
	 * @should delegate if id provided
	 * @should return null if nothing found
	 */
	Concept getConcept(String mapping);
	
	/**
	 * Delegates to {@link ConceptService#getConcept(Integer)}.
	 * <p>
	 * It is a convenience method in case id is passed as an integer and not a string.
	 * 
	 * @param id identifier of the concept
	 * @return the concept or null if not found
	 * @throws APIException reserved for future use
	 * @should return non retired
	 * @should return retired
	 * @should return null if not found
	 */
	Concept getConcept(Integer id);
	
	/**
	 * Purges a local mapping if present in the concept.
	 * 
	 * @param concept purge the local mapping of this concept
	 */
	void purgeLocalMappingInConcept(Concept concept);
	
	/**
	 * Unretires a local mapping if present in the concept.
	 * 
	 * @param concept unretire a local mapping for this concept
	 */
	void markLocalMappingUnretiredInConcept(Concept concept);
	
	/**
	 * Retires a local mapping if present in the concept.
	 * 
	 * @param concept retire a local mapping for this concept
	 */
	void markLocalMappingRetiredInConcept(Concept concept);
	
	/**
	 * Sets the local concept source to the source with the given uuid.
	 * 
	 * @see MetadataMapping#GP_ADD_LOCAL_MAPPINGS
	 * @see #createLocalSourceFromImplementationId()
	 * @param conceptSource concept source to set
	 */
	void setLocalConceptSource(ConceptSource conceptSource);
	
	/**
	 * Save a new metadata source or update an existing one.
	 * @param metadataSource object to save
	 * @return saved object
	 * @since 1.1
	 * @should save valid new object
	 */
	MetadataSource saveMetadataSource(MetadataSource metadataSource);
	
	/**
	 * Get metadata source with the given id.
	 * @param metadataSourceId database id of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	MetadataSource getMetadataSource(Integer metadataSourceId);
	
	/**
	 * Get metadata source with the given uuid. 
	 * @param metadataSourceUuid uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	MetadataSource getMetadataSourceByUuid(String metadataSourceUuid);
	
	/**
	 * Get metadata source with the given name. 
	 * @param metadataSourceName uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	MetadataSource getMetadataSourceByName(String metadataSourceName);
	
	/**
	 * Retire the object and set required info via an AOP injected method.
	 * @param metadataSource object to retire
	 * @param reason reason for retiring the object
	 * @return retired object
	 * @since 1.1
	 * @should retire and set info
	 */
	MetadataSource retireMetadataSource(MetadataSource metadataSource, String reason);
	
	/**
	 * Save a new metadata term mapping or update an existing one.
	 * @param metadataTermMapping object to save
	 * @return saved object
	 * @since 1.1
	 * @should save valid new object
	 * @should fail if code is not unique within source
	 */
	MetadataTermMapping saveMetadataTermMapping(MetadataTermMapping metadataTermMapping);
	
	/**
	 * Batch save for metadata terms mappings.
	 * @param metadataTermMappings collection of metadata term mappings to save
	 * @return collections of saved metadata term mappings
	 * @since 1.1
	 * @see #saveMetadataTermMapping(MetadataTermMapping)
	 */
	Collection<MetadataTermMapping> saveMetadataTermMappings(Collection<MetadataTermMapping> metadataTermMappings);
	
	/**
	 * Get metadata term mapping with the given id.
	 * @param metadataTermMappingId database id of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	MetadataTermMapping getMetadataTermMapping(Integer metadataTermMappingId);
	
	/**
	 * Get metadata term mapping with the given uuid. 
	 * @param metadataTermMappingUuid uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 * @should return matching metadata term mapping
	 */
	MetadataTermMapping getMetadataTermMappingByUuid(String metadataTermMappingUuid);
	
	/**
	 * Find all the unretired metadata term mappings that refer to the given metadata object.
	 * @param referredObject find term mappings that refer to this object
	 * @return list of matching metadata term mappings
	 * @since 1.1
	 * @should return unretired term mappings referring to object
	 */
	List<MetadataTermMapping> getMetadataTermMappings(OpenmrsMetadata referredObject);
	
	/**
	 * Retire the object and set required info via an AOP injected method.
	 * @param metadataTermMapping object to retire
	 * @param reason reason for retiring the object
	 * @return retired object
	 * @since 1.1
	 * @should retire and set info
	 */
	MetadataTermMapping retireMetadataTermMapping(MetadataTermMapping metadataTermMapping, String reason);
	
	/**
	 * Get a specific metadata term mapping from a specific source. 
	 * @param metadataSource source of the term
	 * @param metadataTermCode code of the term   
	 * @return object or null, if does not exist
	 * @since 1.1
	 * @should return a retired term mapping
	 */
	MetadataTermMapping getMetadataTermMapping(MetadataSource metadataSource, String metadataTermCode);
	
	/**
	 * Get all unretired metadata term mappings in the source.
	 * @param metadataSource source of the terms
	 * @return list of terms
	 * @since 1.1
	 * @should return only unretired term mappings
	 */
	List<MetadataTermMapping> getMetadataTermMappings(MetadataSource metadataSource);
	
	/**
	 * Get metadata item referred to by the given metadata term mapping
	 * @param type type of the metadata item
	 * @param metadataSourceName metadata source name
	 * @param metadataTermCode metadata term code
	 * @param <T> type of the metadata item
	 * @return metadata item or null, if not found or if either the metadata term mapping or the metadata item itself are 
	 * retired
	 * @throws InvalidMetadataTypeException when the requested type does not match the type of the metadata item
	 * referred to by the metadata term mapping
	 * @since 1.1
	 * @should return unretired metadata item for unretired term
	 * @should not return retired metadata item for unretired term
	 * @should not return unretired metadata item for retired term
	 * @should fail on type mismatch
	 * @should return null if term does not exist
	 */
	<T extends OpenmrsMetadata> T getMetadataItem(Class<T> type, String metadataSourceName, String metadataTermCode);
	
	/**
	 * Get metadata items of the given type that are referred to by any metadata term mappings in the given metadata source
	 * @param type type of the metadata item
	 * @param metadataSourceName metadata source name
	 * @param <T> type of the metadata item
	 * @return list of matching metadata items
	 * @since 1.1
	 * @should return unretired metadata items of unretired terms matching type
	 * @should return nothing if source does not exist
	 */
	<T extends OpenmrsMetadata> List<T> getMetadataItems(Class<T> type, String metadataSourceName);
	
	/**
	 * Save a new metadata set or update an existing one.
	 * @param metadataSet object to save
	 * @return saved object
	 * @since 1.1
	 * @should save valid new object
	 * @should fail if code is not unique within source
	 */
	MetadataSet saveMetadataSet(MetadataSet metadataSet);
	
	/**
	 * Get metadata set with the given id.
	 * @param metadataSetId database id of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 * @should return a retired set
	 */
	MetadataSet getMetadataSet(Integer metadataSetId);
	
	/**
	 * Get metadata set with the given uuid. 
	 * @param metadataSetUuid uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 * @should return matching metadata set
	 */
	MetadataSet getMetadataSetByUuid(String metadataSetUuid);
	
	/**
	 * Get metadata set with the given name. 
	 * @param metadataSource source of the set
	 * @param metadataSetCode code of the set   
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	MetadataSet getMetadataSet(MetadataSource metadataSource, String metadataSetCode);
	
	/**
	 * Retire the metadata set and its members and set required info via an AOP injected method.
	 * @param metadataSet object to retire
	 * @param reason reason for retiring the object
	 * @return retired object
	 * @since 1.1
	 * @should retire and set info
	 * @should retire members
	 */
	MetadataSet retireMetadataSet(MetadataSet metadataSet, String reason);
	
	/**
	 * Save a new metadata set member or update an existing one.
	 * @param metadataSetMember object to save
	 * @return saved object
	 * @since 1.1
	 * @see #getMetadataSetMembers(MetadataSet, int, int, RetiredHandlingMode)
	 */
	MetadataSetMember saveMetadataSetMember(MetadataSetMember metadataSetMember);
	
	/**
	 * Save a collection of new metadata set members or update an existing ones.
	 * @param metadataSetMembers collection of objects to save
	 * @return the same collection with saved objects
	 * @since 1.1
	 * @see #saveMetadataSetMember(MetadataSetMember)
	 */
	Collection<MetadataSetMember> saveMetadataSetMembers(Collection<MetadataSetMember> metadataSetMembers);
	
	/**
	 * Get metadata set member with the given id.
	 * @param metadataSetMemberId database id of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	MetadataSetMember getMetadataSetMember(Integer metadataSetMemberId);
	
	/**
	 * Get metadata set member with the given uuid. 
	 * @param metadataSetMemberUuid uuid of the object
	 * @return object or null, if does not exist
	 * @since 1.1
	 */
	MetadataSetMember getMetadataSetMemberByUuid(String metadataSetMemberUuid);
	
	/**
	 * Get members of a metadata set. If members have {@link MetadataSetMember#getSortWeight()} set they will be ordered 
	 * in ascending order according to said weight. Note that due to differences in database implementations, the order 
	 * will be unpredictable, if there are null sort weights in the set.
	 * @param metadataSet metadata set
	 * @param firstResult zero based index of first result to get 
	 * @param maxResults maximum number of results to get
	 * @param retiredHandlingMode handle retired objects using this mode
	 * @return list of members in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.1
	 * @should get members in desired order 1
	 * @should respect retire fetch mode 1
	 * @see #getMetadataSetMembers(String, String, int, int, RetiredHandlingMode)
	 */
	List<MetadataSetMember> getMetadataSetMembers(MetadataSet metadataSet, int firstResult, int maxResults,
	        RetiredHandlingMode retiredHandlingMode);
	
	/**
	 * Get members of a metadata set. If members have {@link MetadataSetMember#getSortWeight()} set they will be ordered 
	 * in ascending order according to said weight. Note that due to differences in database implementations, the order 
	 * will be unpredictable, if there are null sort weights in the set.
	 * @param metadataSourceName name of the source of the set
	 * @param metadataSetCode code of the set
	 * @param firstResult zero based index of first result to get 
	 * @param maxResults maximum number of results to get
	 * @param retiredHandlingMode handle retired objects using this mode
	 * @return list of members in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.1
	 * @should get members in desired order 2
	 * @should respect retire fetch mode 2
	 * @see #getMetadataSetMembers(MetadataSet, int, int, RetiredHandlingMode)
	 */
	List<MetadataSetMember> getMetadataSetMembers(String metadataSourceName, String metadataSetCode, int firstResult,
	        int maxResults, RetiredHandlingMode retiredHandlingMode);
	
	/**
	 * Get unretired metadata items in the set. If set members have {@link MetadataSetMember#getSortWeight()} set they will 
	 * be ordered in ascending order according to said weight. Note that due to differences in database implementations, 
	 * the order  will be unpredictable, if there are null sort weights in the set.
	 * @param type type of the metadata items
	 * @param metadataSet metadata set
	 * @param firstResult zero based index of first result to get 
	 * @param maxResults maximum number of results to get
	 * @param <T> type of the metadata items
	 * @return list of items in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.1
	 * @see #getMetadataSetItems(Class, String, String, int, int) 
	 * @should get unretired metadata items of unretired terms matching type in sort weight order 1
	 * @should return nothing if set does not exist
	 */
	<T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, MetadataSet metadataSet, int firstResult,
	        int maxResults);
	
	/**
	 * Get unretired metadata items in the set. If set members have {@link MetadataSetMember#getSortWeight()} set they will 
	 * be ordered in ascending order according to said weight. Note that due to differences in database implementations, 
	 * the order  will be unpredictable, if there are null sort weights in the set.
	 * @param type type of the metadata items
	 * @param metadataSourceName name of the source of the set
	 * @param metadataSetCode code of the set
	 * @param firstResult zero based index of first result to get 
	 * @param maxResults maximum number of results to get
	 * @param <T> type of the metadata items
	 * @return list of items in the order defined by the optional {@link MetadataSetMember#getSortWeight()} values
	 * @since 1.1
	 * @see #getMetadataSetItems(Class, MetadataSet, int, int) 
	 * @should get unretired metadata items of unretired terms matching type in sort weight order 2
	 * @should return nothing if set does not exist
	 */
	<T extends OpenmrsMetadata> List<T> getMetadataSetItems(Class<T> type, String metadataSourceName,
	        String metadataSetCode, int firstResult, int maxResults);
	
	/**
	 * Retire the metadata set member and set required info via an AOP injected method.
	 * @param metadataSetMember object to retire
	 * @param reason reason for retiring the object
	 * @return retired object
	 * @since 1.1
	 * @should retire and set info
	 */
	MetadataSetMember retireMetadataSetMember(MetadataSetMember metadataSetMember, String reason);
}