package com.ly.doc.helper;

import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.model.ApiConfig;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author muyuanjin
 * @date 2024/5/16 下午7:15
 */
class JsonBuildHelperTest {

	/**
	 * com.ly.doc.helper.JsonBuildHelper#buildJson issue:NPE in JsonBuildHelper #789
	 */
	@Test
	void testBuildJsonWithNPE() {
		ApiConfig apiConfig = new ApiConfig();
		JavaProjectBuilder projectBuilder = JavaProjectBuilderHelper.create();
		Assertions.assertNotNull(projectBuilder.getClassByName(getClass().getName()));
		JavaClass taskType = projectBuilder.getClassByName(Task.class.getName());
		Assertions.assertNotNull(taskType);
		Assertions.assertFalse(taskType.getMethods().isEmpty());
		ProjectDocConfigBuilder builder = new ProjectDocConfigBuilder(apiConfig, projectBuilder);
		Assertions.assertNull(builder.getClassByName(getClass().getName()));
		Assertions.assertNotNull(builder.getClassByName(Task.class.getName()));
		Assertions.assertFalse(builder.getClassByName(Task.class.getName()).getMethods().isEmpty());
		String json = JsonBuildHelper.buildJson(Task.class.getName(), Task.class.getCanonicalName(), false, 0,
				new HashMap<>(16), new HashSet<>(), new HashSet<>(), builder);
		System.out.println(json);
	}

	interface Serialize<T extends Serialize<T>> {

		@SuppressWarnings("unchecked")
		default Class<T> getOriginalClass() {
			return (Class<T>) this.getClass();
		}

	}

	public static class Task implements Serialize<Task> {

		private String taskType;

		public String getTaskType() {
			return taskType;
		}

		public void setTaskType(String taskType) {
			this.taskType = taskType;
		}

	}

}