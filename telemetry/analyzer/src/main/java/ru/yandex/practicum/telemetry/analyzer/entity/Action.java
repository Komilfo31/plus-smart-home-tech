package ru.yandex.practicum.telemetry.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@Table(name = "actions")
public class Action {
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "value", nullable = false)
    private Integer value;
}
