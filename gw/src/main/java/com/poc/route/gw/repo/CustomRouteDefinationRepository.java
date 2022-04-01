package com.poc.route.gw.repo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class CustomRouteDefinationRepository implements RouteDefinitionRepository {

	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {

		log.info("Inside getRouteDefinitions >>>");

		List<ApiRouteTO> list = new ArrayList<ApiRouteTO>();

		ApiRouteTO routeTO_1 = new ApiRouteTO();
		routeTO_1.setApiRouteId("1");
		routeTO_1.setName("route_1");
		routeTO_1.setUrl("http://localhost:8080/hello");
		try {
			routeTO_1.setPredicate(objectMapper.readValue(predicate_1, JsonNode.class));
		} catch (Exception ex) {
			log.error("Error", ex);
		}

		list.add(routeTO_1);
		
		List<RouteDefinition> rdList = list.stream().map(this::getRd).collect(Collectors.toList());
		
		return Flux.fromIterable(rdList);

	}

	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return Mono.empty();
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return Mono.empty();
	}

	private RouteDefinition getRd(ApiRouteTO routeTO) {
		RouteDefinition rd = new RouteDefinition();

		// Set URI
		if (Objects.nonNull(routeTO.getUrl())) {
			try {
				rd.setUri(new URI(routeTO.getUrl()));
			} catch (URISyntaxException e) {
				log.error("Malformed URL found ", e);
			}
		}

		// Set predicates
		JsonNode predicateJson = routeTO.getPredicate();
		if (Objects.nonNull(predicateJson)) {
			try {

				String predicateJsonAsString = predicateJson.toString();
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> predicateList = (List<Map<String, Object>>) objectMapper
						.readValue(predicateJsonAsString, List.class);
				List<PredicateDefinition> pdList = new ArrayList<PredicateDefinition>();
				predicateList.forEach(P -> {
					PredicateDefinition pd = new PredicateDefinition();
					if (P.containsKey("name")) {
						pd.setName((String) P.get("name"));
					}

					if (P.containsKey("args")) {
						@SuppressWarnings("unchecked")
						List<Map<String, String>> argsList = (List<Map<String, String>>) P.get("args");
						argsList.forEach(X -> {
							pd.getArgs().putAll(X);
						});
					}
					pdList.add(pd);
				});

				rd.setPredicates(pdList);

			} catch (JsonMappingException e) {
				log.error("Error while parsing predicates", e);
			} catch (JsonProcessingException e) {
				log.error("Error while parsing predicates", e);
			}
		}

		log.info("route {} has been added / refreshed", routeTO.getName());
		return rd;
	}


	private static final String predicate_1 = "[\r\n" + 
			"  {\r\n" + 
			"    \"name\": \"Path\",\r\n" + 
			"    \"args\": [\r\n" + 
			"      {\r\n" + 
			"        \"pattern_0\": \"/hello\"\r\n" + 
			"      }\r\n" + 
			"    ]\r\n" + 
			"  }\r\n" + 
			"]";

}
