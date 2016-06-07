package org.cdrokar.saboteur;

import org.springframework.context.annotation.Import;

@Import(SaboteurConfiguration.class)
public @interface EnableSaboteur {
}
