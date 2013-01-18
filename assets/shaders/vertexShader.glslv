#version 150

uniform mat4 viewMatrix, projMatrix;

in vec3 position;
in vec3 color;

out vec3 colorPS;

void main() {
	vec4 pos = vec4(position.xyz, 1.0);

	colorPS = color;
	gl_Position =   projMatrix * viewMatrix * pos;

}

