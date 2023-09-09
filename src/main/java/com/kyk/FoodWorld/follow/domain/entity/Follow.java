package com.kyk.FoodWorld.follow.domain.entity;

import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.web.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
		uniqueConstraints = {
				@UniqueConstraint(
						name="follow_uk",
						columnNames = {"fromMember_Id", "toMember_Id"}
				)
		}
)
public class Follow extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "follow_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fromMember_Id")
	private Member fromMember;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "toMember_Id")
	private Member toMember;

	public Follow(Member fromMember, Member toMember) {
		this.fromMember = fromMember;
		this.toMember = toMember;
	}
}



